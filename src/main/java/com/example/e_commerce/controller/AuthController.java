package com.example.e_commerce.controller;

import com.example.e_commerce.common.Result;
import com.example.e_commerce.dto.LoginRequest;
import com.example.e_commerce.dto.LoginResponse;
import com.example.e_commerce.entity.User;
import com.example.e_commerce.service.UserService;
import com.example.e_commerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // 获取用户信息
            User user = userService.findByUsername(loginRequest.getUsername());
            
            LoginResponse response = LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("用户名或密码错误");
        }
    }
    
    @PostMapping("/refresh")
    public Result refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
                String username = jwtUtil.getUsernameFromToken(refreshToken);
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(refreshToken, userDetails)) {
                    String newToken = jwtUtil.refreshToken(refreshToken);
                    return Result.success(newToken);
                }
            }
            return Result.error("刷新token无效");
        } catch (Exception e) {
            return Result.error("刷新token失败");
        }
    }
    
    @PostMapping("/logout")
    public Result logout() {
        // 可以在这里将token加入黑名单
        return Result.success("退出成功");
    }
}