package hei.school.ingredientmanagementapi.controller;

import hei.school.ingredientmanagementapi.entity.Dish;
import hei.school.ingredientmanagementapi.entity.Ingredient;
import hei.school.ingredientmanagementapi.exception.BadRequestException;
import hei.school.ingredientmanagementapi.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        try {
            return ResponseEntity.ok(dishService.getAll());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<Ingredient> ingredients) {
        try {
            Dish dish = dishService.updateIngredients(id, ingredients);
            if (dish == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Content-Type", "text/plain")
                        .body("Dish.id=" + id + " is not found");
            }
            return ResponseEntity.ok(dish);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}