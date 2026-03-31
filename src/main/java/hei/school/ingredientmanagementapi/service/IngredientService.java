package hei.school.ingredientmanagementapi.service;

import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.entity.StockValue;
import hei.school.ingredientmanagementapi.entity.UnitEnum;
import hei.school.ingredientmanagementapi.exception.BadRequestException;
import hei.school.ingredientmanagementapi.repository.IngredientRepository;
import hei.school.ingredientmanagementapi.validator.IngredientValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientValidator ingredientValidator;

    public IngredientService(IngredientRepository ingredientRepository,
                             IngredientValidator ingredientValidator) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientValidator = ingredientValidator;
    }

    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient getById(int id) {
        return ingredientRepository.findById(id);
    }

    public StockValue getStockAt(int id, String at, String unit) throws BadRequestException {
        ingredientValidator.validateStockParams(at, unit);
        Ingredient ingredient = ingredientRepository.findById(id);
        if (ingredient == null) {
            return null;
        }
        ingredient.setStockMovementList(ingredientRepository.findStockMovements(id));
        Instant instant = Instant.parse(at);
        UnitEnum unitEnum = UnitEnum.valueOf(unit.toUpperCase());
        StockValue sv = ingredient.getStockValueAt(instant);
        sv.setUnit(unitEnum);
        return sv;
    }
}