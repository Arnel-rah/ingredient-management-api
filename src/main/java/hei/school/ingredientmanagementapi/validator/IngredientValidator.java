package hei.school.ingredientmanagementapi.validator;

import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IngredientValidator {

    public void validateStockParams(String at, String unit) throws BadRequestException {
        StringBuilder message = new StringBuilder();
        if (at == null || at.isBlank()) {
            message.append("Either mandatory query parameter `at` or `unit` is not provided.");
        }
        if (unit == null || unit.isBlank()) {
            message.append("Either mandatory query parameter `at` or `unit` is not provided.");
        }
        if (!message.isEmpty()) {
            throw new BadRequestException(message.toString());
        }
    }

    public void validateIngredientList(List<Ingredient> ingredients) throws BadRequestException {
        StringBuilder message = new StringBuilder();
        if (ingredients == null) {
            message.append("Request body with ingredient list is required.");
        }
        if (!message.isEmpty()) {
            throw new BadRequestException(message.toString());
        }
    }
}