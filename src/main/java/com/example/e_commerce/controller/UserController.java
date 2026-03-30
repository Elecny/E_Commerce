package com.example.e_commerce.controller;

import com.example.e_commerce.dto.UserCreateDTO;
import com.example.e_commerce.dto.UserResponse;
import com.example.e_commerce.service.UserService;
import com.example.e_commerce.common.Result;
import com.example.e_commerce.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 创建用户
    @PostMapping("/create")
    public Result<UserResponse> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        User tcreate = new User();
        tcreate.setEmail(userCreateDTO.getEmail());
        tcreate.setPassword(userCreateDTO.getPassword());
        tcreate.setUsername(userCreateDTO.getUsername());
        User savedUser = userService.createUser(tcreate);
        UserResponse response = UserResponse.tResponse(savedUser);
        return Result.success(response);
    }

    // 查询所有用户
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        List<User> list = userService.getAllUsers();
        return Result.success(list);
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(Result::success).orElseGet(() -> Result.error("用户不存在"));
    }

    // 根据用户名查询
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(Result::success).orElseGet(() -> Result.error("用户不存在"));
    }

    // 更新用户
    @PutMapping("/update/{id}")
    public Result<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return Result.success(updatedUser);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    // 启用/禁用用户
    @PutMapping("/toggle/{id}")
    public Result<User> toggleUserStatus(@PathVariable Long id) {
        User user = userService.toggleUserStatus(id);
        return Result.success(user);
    }

    // 修改密码
    @PostMapping("/change-pwd/{id}")
    public Result<?> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }
}