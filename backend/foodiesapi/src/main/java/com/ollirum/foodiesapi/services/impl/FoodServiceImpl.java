package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import com.ollirum.foodiesapi.entities.Food;
import com.ollirum.foodiesapi.mappers.FoodMapper;
import com.ollirum.foodiesapi.repositories.FoodRepository;
import com.ollirum.foodiesapi.services.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FoodServiceImpl implements FoodService {
    private final S3Client s3Client;
    private final FoodRepository foodRepository;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FoodResponseDTO addFood(FoodRequestDTO inputDTO, MultipartFile file) {
        Food newFood = FoodMapper.toEntity(inputDTO);
        String s3Key = uploadFile(file);

        try {
            String imageUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
            newFood.setImageUrl(imageUrl);
            newFood = foodRepository.save(newFood);

            return FoodMapper.toResponseDTO(newFood);
        } catch (Exception e) {
            deleteFile(s3Key);
            throw e;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<FoodResponseDTO> findAllFoods() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream().map(FoodMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public FoodResponseDTO findById(String id) {
        Food food = foodRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return FoodMapper.toResponseDTO(food);
    }

    private String uploadFile(MultipartFile file) {
        String filenameExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + filenameExtension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
                return key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteFile(String key) {
        try {
            s3Client.deleteObject(builder ->
                    builder.bucket(bucketName).key(key));
        } catch (SdkClientException e) {
            System.err.println(e.getMessage());
        }
    }
}