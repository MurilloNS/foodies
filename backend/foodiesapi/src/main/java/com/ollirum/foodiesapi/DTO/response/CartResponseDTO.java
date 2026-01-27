package com.ollirum.foodiesapi.DTO.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record CartResponseDTO(String id, String userId, Map<String, Integer> items) {
}