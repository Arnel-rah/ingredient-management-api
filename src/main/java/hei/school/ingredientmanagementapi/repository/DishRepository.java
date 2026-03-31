package hei.school.ingredientmanagementapi.repository;

import hei.school.ingredientmanagementapi.entity.*;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("ing_id"),
                rs.getString("ing_name"),
                rs.getDouble("ing_price"),
                CategoryIngredient.valueOf(rs.getString("ing_category"))
        );
    }
    public List<Dish> findAll(Double priceUnder, Double priceOver, String name) {
        StringBuilder sql = new StringBuilder(
                "SELECT d.id, d.name, d.dish_type, d.selling_price, " +
                        "       i.id AS ing_id, i.name AS ing_name, " +
                        "       i.price AS ing_price, i.category AS ing_category " +
                        "FROM dish d " +
                        "LEFT JOIN dishingredient di ON di.id_dish = d.id " +
                        "LEFT JOIN ingredient i ON i.id = di.id_ingredient " +
                        "WHERE 1=1 "
        );

        if (priceUnder != null) sql.append(" AND d.selling_price <= ? ");
        if (priceOver != null) sql.append(" AND d.selling_price >= ? ");
        if (name != null) sql.append(" AND d.name ILIKE ? ");

        sql.append(" ORDER BY d.id, i.id");

        Map<Integer, Dish> map = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (priceUnder != null) ps.setDouble(idx++, priceUnder);
            if (priceOver != null) ps.setDouble(idx++, priceOver);
            if (name != null) ps.setString(idx++, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dishId = rs.getInt("id");

                    Dish dish = map.computeIfAbsent(dishId, k -> {
                        try {
                            Dish d = new Dish();
                            d.setId(dishId);
                            d.setName(rs.getString("name"));
                            d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                            double sp = rs.getDouble("selling_price");
                            d.setSellingPrice(rs.wasNull() ? null : sp);

                            d.setIngredients(new ArrayList<>());
                            return d;
                        } catch (SQLException e) {
                            throw new RuntimeException("Error while mapping dish", e);
                        }
                    });

                    if (rs.getObject("ing_id") != null) {
                        dish.getIngredients().add(mapIngredient(rs));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching dishes", e);
        }

        return new ArrayList<>(map.values());
    }

    public Dish findById(int id) {
        String sql =
                "SELECT d.id, d.name, d.dish_type, d.selling_price, " +
                        "       i.id AS ing_id, i.name AS ing_name, " +
                        "       i.price AS ing_price, i.category AS ing_category " +
                        "FROM dish d " +
                        "LEFT JOIN dishingredient di ON di.id_dish = d.id " +
                        "LEFT JOIN ingredient i ON i.id = di.id_ingredient " +
                        "WHERE d.id = ? ORDER BY i.id";

        Dish dish = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (dish == null) {
                        dish = new Dish();
                        dish.setId(rs.getInt("id"));
                        dish.setName(rs.getString("name"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                        double sp = rs.getDouble("selling_price");
                        dish.setSellingPrice(rs.wasNull() ? null : sp);

                        dish.setIngredients(new ArrayList<>());
                    }

                    if (rs.getObject("ing_id") != null) {
                        dish.getIngredients().add(mapIngredient(rs));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching dish", e);
        }

        return dish;
    }

    public Dish updateIngredients(int dishId, List<Integer> ingredientIds) {
        String deleteSql = "DELETE FROM dishingredient WHERE id_dish = ?";
        String insertSql = "INSERT INTO dishingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, 1, 'KG')";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement(deleteSql);
                 PreparedStatement ins = conn.prepareStatement(insertSql)) {

                del.setInt(1, dishId);
                del.executeUpdate();

                if (ingredientIds != null) {
                    for (int ingId : ingredientIds) {
                        ins.setInt(1, dishId);
                        ins.setInt(2, ingId);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error while updating ingredients", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
        return findById(dishId);
    }

    public List<Dish> addDishes(List<Dish> dishes) {
        String checkSql = "SELECT 1 FROM dish WHERE name = ?";
        String insertDishSql = "INSERT INTO dish (name, dish_type, selling_price) VALUES (?, ?::dish_type, ?) RETURNING id";
        String insertRelSql = "INSERT INTO dishingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, 1, 'KG')";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Dish dish : dishes) {
                    try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                        psCheck.setString(1, dish.getName());
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next()) {
                                throw new RuntimeException("Dish.name=" + dish.getName() + " already exists");
                            }
                        }
                    }

                    try (PreparedStatement psDish = conn.prepareStatement(insertDishSql)) {
                        psDish.setString(1, dish.getName());
                        psDish.setString(2, dish.getDishType().name());
                        if (dish.getSellingPrice() == null) psDish.setNull(3, Types.NUMERIC);
                        else psDish.setDouble(3, dish.getSellingPrice());

                        try (ResultSet rs = psDish.executeQuery()) {
                            if (rs.next()) dish.setId(rs.getInt(1));
                        }
                    }

                    if (dish.getIngredients() != null && !dish.getIngredients().isEmpty()) {
                        try (PreparedStatement psRel = conn.prepareStatement(insertRelSql)) {
                            for (Ingredient ing : dish.getIngredients()) {
                                psRel.setInt(1, dish.getId());
                                psRel.setInt(2, ing.getId());
                                psRel.addBatch();
                            }
                            psRel.executeBatch();
                        }
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }
}