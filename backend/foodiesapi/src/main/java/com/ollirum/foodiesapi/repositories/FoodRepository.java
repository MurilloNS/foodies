package com.ollirum.foodiesapi.repositories;

import com.ollirum.foodiesapi.entities.Food;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodRepository extends MongoRepository<Food, String> {
}