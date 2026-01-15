package com.ollirum.foodiesapi.DTO.request;

import lombok.Builder;

@Builder
public record UserRequestDTO(String name, String email, String password) {
}