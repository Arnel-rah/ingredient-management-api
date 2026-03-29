package hei.school.ingredientmanagementapi.validator;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class IngredientValidator {
    public void validateStockParams(Instant at, String unit) {
        if (at == null || unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Parameter 'at' or 'unit' cannot be null or blank");
        }
    }
}
