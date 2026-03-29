package hei.school.ingredientmanagementapi.service;

import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.entity.StockValue;
import hei.school.ingredientmanagementapi.entity.UnitEnum;
import hei.school.ingredientmanagementapi.repository.IngredientRepository;
import hei.school.ingredientmanagementapi.validator.IngredientValidator;

import java.time.Instant;
import java.util.List;

public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientValidator ingredientValidator;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientValidator = new IngredientValidator();
    }

    // GET /ingredients
    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();      // était findAll(), aligné avec le repository
    }

    // GET /ingredients/{id}
    public Ingredient getById(int id) {
        return ingredientRepository.findById(id);   // NotFoundException levée dans le repository
    }

    // GET /ingredients/{id}/stock?at=...&unit=...
    public StockValue getStockAt(int id, String at, String unit) {
        // 1. Validation des paramètres (400 si manquants)
        ingredientValidator.validateStockParams(at, unit);

        // 2. Récupérer l'ingrédient (404 si non trouvé)
        Ingredient ingredient = ingredientRepository.findById(id);

        // 3. Charger les mouvements de stock
        ingredient.setStockMovementList(ingredientRepository.findStockMovements(id));

        // 4. Calculer le stock à l'instant t
        Instant instant = Instant.parse(at);
        UnitEnum unitEnum = UnitEnum.valueOf(unit.toUpperCase());

        StockValue sv = ingredient.getStockValueAt(instant);
        sv.setUnit(unitEnum);
        return sv;
    }
}