package hei.school.ingredientmanagementapi.controller;

import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.entity.StockValue;
import hei.school.ingredientmanagementapi.repository.IngredientRepository;
import hei.school.ingredientmanagementapi.service.IngredientService;
import hei.school.ingredientmanagementapi.validator.IngredientValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientService = new IngredientService(ingredientRepository);
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable int id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<StockValue> getIngredientStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {
        return ResponseEntity.ok(ingredientService.getStockAt(id, at, unit));
    }
}