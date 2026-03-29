package hei.school.ingredientmanagementapi.validator;

import hei.school.ingredientmanagementapi.exception.BadRequestException;
import hei.school.ingredientmanagementapi.entity.Ingredient;

import java.util.List;

public class IngredientValidator {
    public void validateStockParams(String at, String unit) {
        if (at == null || unit == null) {
            throw new BadRequestException(
                    "Either mandatory query parameter `at` or `unit` is not provided."
            );
        }
    }

    public void validateIngredientList(List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new BadRequestException(
                    "Request body with ingredient list is required."
            );
        }
    }
}