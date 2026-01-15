package com.ollirum.foodiesapi.DTO.response;

import lombok.Builder;

@Builder
public record UserResponseDTO(String id, String name, String email) {
}