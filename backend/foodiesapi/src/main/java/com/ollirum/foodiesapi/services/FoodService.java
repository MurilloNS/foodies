package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    FoodResponseDTO addFood(FoodRequestDTO inputDTO, MultipartFile file);
}