package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import com.ollirum.foodiesapi.entities.Food;
import com.ollirum.foodiesapi.repositories.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceImplTest {
    @InjectMocks
    private FoodServiceImpl foodService;

    @Mock
    private S3Client s3Client;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(foodService, "bucketName", "test-bucket");
    }

    @Test
    void shouldAddFoodSuccessfully() throws Exception {
        // Given
        FoodRequestDTO requestDTO = new FoodRequestDTO("Pizza", "Desc", BigDecimal.valueOf(29.90), "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn("pizza.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn("image".getBytes());

        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder()
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                .build();

        when(s3Client.putObject(
                any(software.amazon.awssdk.services.s3.model.PutObjectRequest.class),
                any(software.amazon.awssdk.core.sync.RequestBody.class))).thenReturn(putObjectResponse);

        when(foodRepository.save(any(Food.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Action
        FoodResponseDTO responseDTO = foodService.addFood(requestDTO, multipartFile);

        // Verify
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.imageUrl());
        assertTrue(responseDTO.imageUrl().contains("test-bucket.s3.amazonaws.com"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    void shouldDeleteFileWhenSavingFoodFails() throws Exception {
        // Given
        FoodRequestDTO requestDTO = new FoodRequestDTO("Pizza", "Desc", BigDecimal.TEN, "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn("pizza.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn("image".getBytes());

        PutObjectResponse putObjectResponse = (PutObjectResponse) PutObjectResponse.builder()
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                .build();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(putObjectResponse);

        when(foodRepository.save(any(Food.class)))
                .thenThrow(new RuntimeException());

        // Action
        assertThrows(RuntimeException.class, () -> foodService.addFood(requestDTO, multipartFile));

        // Verify
        verify(s3Client).deleteObject(any(java.util.function.Consumer.class));
    }

    @Test
    void shouldThrowExceptionWhenSdkHttpResponseIsNull() throws Exception {
        FoodRequestDTO requestDTO =
                new FoodRequestDTO("Pizza", "Desc", BigDecimal.TEN, "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn("pizza.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn("image".getBytes());

        PutObjectResponse responseWithoutHttp =
                PutObjectResponse.builder().build();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(responseWithoutHttp);

        assertThrows(NullPointerException.class,
                () -> foodService.addFood(requestDTO, multipartFile));
    }

    @Test
    void shouldThrowExceptionWhenOriginalFilenameIsNull() throws Exception {
        FoodRequestDTO requestDTO =
                new FoodRequestDTO("Pizza", "Desc", BigDecimal.TEN, "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> foodService.addFood(requestDTO, multipartFile));

        verify(s3Client, never())
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldThrowExceptionWhenS3UploadFails() throws Exception {
        FoodRequestDTO requestDTO = new FoodRequestDTO("Pizza", "Desc", BigDecimal.TEN, "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn("pizza.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenReturn("image".getBytes());

        PutObjectResponse errorResponse = (PutObjectResponse) PutObjectResponse.builder()
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                .build();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(errorResponse);

        assertThrows(ResponseStatusException.class,
                () -> foodService.addFood(requestDTO, multipartFile));

        verify(foodRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFileReadFails() throws Exception {
        FoodRequestDTO requestDTO = new FoodRequestDTO("Pizza", "Desc", BigDecimal.TEN, "CATEGORY");

        when(multipartFile.getOriginalFilename()).thenReturn("pizza.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getBytes()).thenThrow(new IOException("IO error"));

        assertThrows(ResponseStatusException.class,
                () -> foodService.addFood(requestDTO, multipartFile));

        verify(s3Client, never()).putObject(
                any(software.amazon.awssdk.services.s3.model.PutObjectRequest.class),
                any(software.amazon.awssdk.core.sync.RequestBody.class)
        );
    }

    @Test
    void shouldReturnAllFoods() {
        // Given
        Food food = new Food();
        food.setId("1");
        food.setName("Pizza");

        when(foodRepository.findAll()).thenReturn(List.of(food));

        // Action
        List<FoodResponseDTO> result = foodService.findAllFoods();

        assertEquals(1, result.size());
        verify(foodRepository).findAll();
    }

    @Test
    void shouldFindFoodById() {
        // Given
        Food food = new Food();
        food.setId("1");
        food.setName("Pizza");

        when(foodRepository.findById("1")).thenReturn(Optional.of(food));

        // Action
        FoodResponseDTO responseDTO = foodService.findById("1");

        // Verify
        assertNotNull(responseDTO);
        verify(foodRepository).findById("1");
    }

    @Test
    void shouldThrowNotFoundWhenFoodDoesNotExist() {
        when(foodRepository.findById("99")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> foodService.findById("99")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldDeleteFoodSuccessfully() {
        // Given
        Food food = new Food();
        food.setId("1");
        food.setImageUrl("https://test-bucket.s3.amazonaws.com/test.jpg");

        when(foodRepository.findById("1")).thenReturn(Optional.of(food));

        // Action
        foodService.deleteFood("1");

        // verify
        verify(foodRepository).deleteById("1");
        verify(s3Client).deleteObject(any(java.util.function.Consumer.class));
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistingFood() {
        when(foodRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> foodService.deleteFood("99"));

        verify(s3Client, never())
                .deleteObject(any(java.util.function.Consumer.class));

        verify(foodRepository, never()).deleteById(any());
    }

    @Test
    void shouldIgnoreExceptionWhenS3DeleteFails() {
        Food food = new Food();
        food.setId("1");
        food.setImageUrl("https://test-bucket.s3.amazonaws.com/test.jpg");

        when(foodRepository.findById("1")).thenReturn(Optional.of(food));

        doThrow(SdkClientException.builder().message("S3 down").build())
                .when(s3Client)
                .deleteObject(any(java.util.function.Consumer.class));

        assertDoesNotThrow(() -> foodService.deleteFood("1"));

        verify(foodRepository).deleteById("1");
    }
}