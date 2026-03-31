package hei.school.ingredientmanagementapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Dish {
    private Integer id;
    private String name;

    @JsonProperty("dish_type")
    private DishTypeEnum dishType;

    @JsonProperty("selling_price")
    private Double sellingPrice;
    private List<Ingredient> ingredients;

    public Dish() {}

    public Dish(int id, String name, DishTypeEnum dishType, Double sellingPrice, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.sellingPrice = sellingPrice;
        this.ingredients = ingredients;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}