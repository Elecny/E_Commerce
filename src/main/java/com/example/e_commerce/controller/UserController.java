package com.example.e_commerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.e_commerce.entity.User;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public Result getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User User = (User) authentication.getPrincipal();
        
        // 获取当前登录用户信息
        return Result.success(userDetails);
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result adminOnly() {
        return Result.success("只有管理员可以访问");
    }
}