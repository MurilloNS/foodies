package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {
    FoodResponseDTO addFood(FoodRequestDTO inputDTO, MultipartFile file);
    List<FoodResponseDTO> findAllFoods();
    FoodResponseDTO findById(String id);
    void deleteFood(String id);
}