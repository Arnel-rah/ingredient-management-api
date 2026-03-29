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
    public List<Dish> findAll() {
        String sql =
                "SELECT d.id, d.name, d.dish_type, d.selling_price, " +
                        "       i.id AS ing_id, i.name AS ing_name, " +
                        "       i.price AS ing_price, i.category AS ing_category " +
                        "FROM dish d " +
                        "LEFT JOIN dishingredient di ON di.id_dish = d.id " +
                        "LEFT JOIN ingredient i ON i.id = di.id_ingredient " +
                        "ORDER BY d.id, i.id";

        Map<Integer, Dish> map = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
                        throw new RuntimeException(e);
                    }
                });

                int ingId = rs.getInt("ing_id");
                if (!rs.wasNull()) {
                    dish.getIngredients().add(mapIngredient(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats", e);
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
                        "WHERE d.id = ? " +
                        "ORDER BY i.id";

        Dish dish = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

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
                int ingId = rs.getInt("ing_id");
                if (!rs.wasNull()) {
                    dish.getIngredients().add(mapIngredient(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat", e);
        }

        if (dish == null) {
            throw new RuntimeException("Dish.id=" + id + " is not found");
        }
        return dish;
    }

    public Dish updateIngredients(int dishId, List<Integer> ingredientIds) {
        findById(dishId);

        String deleteSql = "DELETE FROM dishingredient WHERE id_dish = ?";
        String insertSql =
                "INSERT INTO dishingredient (id_dish, id_ingredient, quantity_required, unit) " +
                        "SELECT ?, ?, 1, 'KG' " +
                        "WHERE EXISTS (SELECT 1 FROM ingredient WHERE id = ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement del = conn.prepareStatement(deleteSql);
                del.setInt(1, dishId);
                del.executeUpdate();
                for (int ingId : ingredientIds) {
                    PreparedStatement ins = conn.prepareStatement(insertSql);
                    ins.setInt(1, dishId);
                    ins.setInt(2, ingId);
                    ins.setInt(3, ingId);
                    ins.executeUpdate();
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la mise à jour des ingrédients", e);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion", e);
        }
        return findById(dishId);
    }
}