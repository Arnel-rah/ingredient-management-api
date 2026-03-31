package hei.school.ingredientmanagementapi.service;

import hei.school.ingredientmanagementapi.entity.Dish;
import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.exception.BadRequestException;
import hei.school.ingredientmanagementapi.repository.DishRepository;
import hei.school.ingredientmanagementapi.validator.IngredientValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientValidator ingredientValidator;

    public DishService(DishRepository dishRepository,
                       IngredientValidator ingredientValidator) {
        this.dishRepository = dishRepository;
        this.ingredientValidator = ingredientValidator;
    }

    public List<Dish> getAll(Double priceUnder, Double priceOver, String name) {
        return dishRepository.findAll(priceUnder, priceOver, name);
    }

    public Dish updateIngredients(int dishId, List<Ingredient> ingredients) throws BadRequestException {
        ingredientValidator.validateIngredientList(ingredients);
        List<Integer> ids = ingredients.stream()
                .map(Ingredient::getId)
                .toList();
        return dishRepository.updateIngredients(dishId, ids);
    }

    public List<Dish> saveAll(List<Dish> dishes) {
        return dishRepository.addDishes(dishes);
    }
}