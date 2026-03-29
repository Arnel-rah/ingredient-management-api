package hei.school.ingredientmanagementapi.service;

import hei.school.ingredientmanagementapi.entity.Dish;
import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.repository.DishRepository;
import hei.school.ingredientmanagementapi.validator.IngredientValidator;

import java.util.List;

public class DishService {

    private final DishRepository dishRepository;
    private final IngredientValidator ingredientValidator;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
        this.ingredientValidator = new IngredientValidator();
    }
    public List<Dish> getAll() {
        return dishRepository.findAll();
    }

    public Dish updateIngredients(int dishId, List<Ingredient> ingredients) {
        ingredientValidator.validateIngredientList(ingredients);
        List<Integer> ids = ingredients.stream()
                .map(Ingredient::getId)
                .toList();

        return dishRepository.updateIngredients(dishId, ids);
    }
}