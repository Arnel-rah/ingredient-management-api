package hei.school.ingredientmanagementapi.controller;

import hei.school.ingredientmanagementapi.entity.Dish;
import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.repository.DishRepository;
import hei.school.ingredientmanagementapi.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishRepository dishRepository) {
        this.dishService = new DishService(dishRepository);
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAll());
    }
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<Dish> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<Ingredient> ingredients) {
        return ResponseEntity.ok(dishService.updateIngredients(id, ingredients));
    }
}