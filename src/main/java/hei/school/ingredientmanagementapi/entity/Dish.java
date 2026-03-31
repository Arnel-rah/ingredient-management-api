package hei.school.ingredientmanagementapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Dish {
    private Integer id;
    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    @JsonProperty("dish_type")
    private DishTypeEnum dishType;

    @Setter
    @Getter
    @JsonProperty("selling_price")
    private Double sellingPrice;
    @Setter
    @Getter
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

}