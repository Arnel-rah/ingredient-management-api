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
    private Ingredient mapRow(ResultSet rs) {
       try {
           return new Ingredient(
                   rs.getInt("id"),
                   rs.getString("name"),
                   rs.getDouble("price"),
                   CategoryIngredient.valueOf(rs.getString("category"))
           );
       }catch (SQLException e){
           throw new RuntimeException(e);
       }
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
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("La catégorie en BDD ne correspond à aucun Enum Java", e);
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

            throw new RuntimeException("Ingredient.id=" + id + " is not found");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'ingrédient", e);
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
                StockMovement sm = new StockMovement(
                        rs.getInt("id"),
                        value,
                        MovementTypeEnum.valueOf(rs.getString("type")),
                        rs.getTimestamp("creation_datetime").toInstant()
                );
                list.add(sm);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des mouvements de stock", e);
        }

        return list;
    }
}