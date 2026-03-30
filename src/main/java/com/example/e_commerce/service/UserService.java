package com.example.e_commerce.service;

import com.example.e_commerce.entity.User;
import com.example.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 创建用户
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("this name already exists,change to another one");
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("this email site has been used,please change to another one");
        }
        
        // 设置创建时间
        user.setCreateAt(LocalDateTime.now());
        user.setEnabled(true);
        
        // 密码加密（实际项目中应该加密）
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    // 查询所有用户
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 根据ID查询用户
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // 根据用户名查询用户
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 更新用户信息
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新字段
        if (userDetails.getUsername() != null) {
            // 检查新用户名是否已被其他用户使用
            if (!user.getUsername().equals(userDetails.getUsername()) && 
                userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(userDetails.getUsername());
        }
        
        if (userDetails.getEmail() != null) {
            // 检查新邮箱是否已被其他用户使用
            if (!user.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getPassword() != null) {
            // 密码加密
            // user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            user.setPassword(userDetails.getPassword());
        }
        
        return userRepository.save(user);
    }
    
    // 删除用户
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    // 启用/禁用用户
    public User toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }
    
    // 修改密码
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 验证旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("wrong password");
        }
        
        // 设置新密码
        // user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}