package hei.school.ingredientmanagementapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryIngredient category;

    @JsonIgnore
    private List<StockMovement> stockMovementList;

    public Ingredient() {}
    public Ingredient(int id, String name, double price, CategoryIngredient category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public Ingredient(int id, String name, double price, CategoryIngredient category,
                      List<StockMovement> stockMovementList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockMovementList = stockMovementList;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public CategoryIngredient getCategory() { return category; }
    public void setCategory(CategoryIngredient category) { this.category = category; }

    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }
    public StockValue getStockValueAt(Instant t) {
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return new StockValue(0, UnitEnum.KG);
        }
        double quantity = stockMovementList.stream()
                .filter(sm -> !sm.getCreationDatetime().isAfter(t))
                .mapToDouble(sm -> sm.getType() == MovementTypeEnum.IN
                        ?  sm.getValue().getQuantity()
                        : -sm.getValue().getQuantity())
                .sum();
        return new StockValue(quantity, UnitEnum.KG);
    }
}