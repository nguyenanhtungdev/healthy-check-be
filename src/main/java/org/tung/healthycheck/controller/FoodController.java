package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tung.healthycheck.dto.FoodDTO;
import org.tung.healthycheck.services.FoodService;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodDTO>> listAllFoods() {
        return ResponseEntity.ok(foodService.listAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(foodService.search(keyword));
    }
}
