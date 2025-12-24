package com.ollirum.foodiesapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import com.ollirum.foodiesapi.services.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final FoodService foodService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FoodResponseDTO> addFood(@RequestPart("food") String foodString, @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequestDTO requestDTO = null;

        try {
            requestDTO = objectMapper.readValue(foodString, FoodRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        FoodResponseDTO responseDTO = foodService.addFood(requestDTO, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<FoodResponseDTO>> findAllFoods() {
        List<FoodResponseDTO> foods = foodService.findAllFoods();
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDTO> findById(@PathVariable String id) {
        FoodResponseDTO food = foodService.findById(id);
        return ResponseEntity.ok(food);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable String id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}