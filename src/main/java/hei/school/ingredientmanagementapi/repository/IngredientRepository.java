package hei.school.ingredientmanagementapi.repository;

import hei.school.ingredientmanagementapi.entity.*;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                CategoryIngredient.valueOf(rs.getString("category"))
        );
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ingredients.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching ingredients", e);
        }

        return ingredients;
    }

    public Ingredient findById(int id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching ingredient", e);
        }
    }

    public List<StockMovement> findStockMovements(int ingredientId) {
        List<StockMovement> list = new ArrayList<>();

        String sql = "SELECT id, quantity, type, unit, creation_datetime " +
                "FROM stockmovement WHERE id_ingredient = ? ORDER BY creation_datetime";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StockValue value = new StockValue(
                        rs.getDouble("quantity"),
                        UnitEnum.valueOf(rs.getString("unit"))
                );

                list.add(new StockMovement(
                        rs.getInt("id"),
                        value,
                        MovementTypeEnum.valueOf(rs.getString("type")),
                        rs.getTimestamp("creation_datetime").toInstant()
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching stock movements", e);
        }

        return list;
    }
}