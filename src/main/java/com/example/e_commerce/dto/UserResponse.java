package com.example.e_commerce.dto;

import com.example.e_commerce.entity.User;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled = true;
    public UserResponse(){}
    public UserResponse(Long id, String username, Boolean enabled,String email) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.email = email;
    }
    public static UserResponse tResponse(User usr){
        UserResponse response = new UserResponse();
        response.setEmail(usr.getEmail());
        response.setEnabled(usr.getEnabled());
        response.setId(usr.getId());
        response.setUsername(usr.getUsername());
        return response;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
