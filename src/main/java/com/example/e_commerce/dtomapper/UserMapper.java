package com.example.e_commerce.dtomapper;


import com.example.e_commerce.dto.UserResponse;
import com.example.e_commerce.entity.User;

public class UserMapper {
    
    // Entity to DTO
    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
    
    // DTO to Entity
    public  static User toEntity(User request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Note: Should be encoded  
        return user;
    }
    
    // Update existing entity from DTO
    
}