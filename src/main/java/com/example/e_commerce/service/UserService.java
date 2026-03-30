package com.example.e_commerce.service;

import com.example.e_commerce.dto.UserCreateDTO;
import com.example.e_commerce.entity.User;
import com.example.e_commerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @CacheEvict(value = {"users", "userList"}, allEntries = true)
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("this name already exists,change to another one");
        }
        
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("this email site has been used,please change to another one");
        }
        
        user.setCreateAt(LocalDateTime.now());
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    // 查询所有用户
    @Cacheable(value = "userList", unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 根据ID查询用户
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // 根据用户名查询用户
    @Cacheable(value = "users", key = "'username:' + #username")
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 更新用户信息
    @Caching(
        put = {
            @CachePut(value = "users", key = "#result.id"),
            @CachePut(value = "users", key = "'username:' + #result.username")
        },
        evict = {
            @CacheEvict(value = "userList", allEntries = true)
        }
    )
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (userDetails.getUsername() != null) {
            if (!user.getUsername().equals(userDetails.getUsername()) && 
                userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(userDetails.getUsername());
        }
        
        if (userDetails.getEmail() != null) {
            if (!user.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }
        
        return userRepository.save(user);
    }
    
    // 删除用户
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userList", allEntries = true)
    })
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    // 安全更新用户信息
    @Caching(
        put = {
            @CachePut(value = "users", key = "#result.id"),
            @CachePut(value = "users", key = "'username:' + #result.username")
        },
        evict = {
            @CacheEvict(value = "userList", allEntries = true)
        }
    )
    public User updateUserInfo(Long id, UserCreateDTO dto) {
        User user = getUserById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!user.getUsername().equals(dto.getUsername()) && 
            userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (!user.getEmail().equals(dto.getEmail()) && 
            userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        
        return userRepository.save(user);
    }

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

        // 启用/禁用用户
    public User toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }
}