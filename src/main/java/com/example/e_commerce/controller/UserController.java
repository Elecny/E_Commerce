package com.example.e_commerce.controller;

import com.example.e_commerce.dto.UserCreateDTO;
import com.example.e_commerce.dto.UserResponse;
import com.example.e_commerce.common.Result;
import com.example.e_commerce.entity.User;
import com.example.e_commerce.service.UserService;
import com.example.e_commerce.utils.ThreadPoolUtil; // 线程池工具
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ====================== 创建用户（支持高并发异步）======================
    @PostMapping("/create")
    public Result<UserResponse> createUser(@RequestBody UserCreateDTO dto) {
        // 异步提交任务（高并发专用）
        Future<User> future = ThreadPoolUtil.submit(() -> {
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword()); // 建议在Service加密
            user.setEmail(dto.getEmail());
            user.setEnabled(true);
            return userService.createUser(user);
        });

        try {
            User saved = future.get();
            return Result.success(UserResponse.tResponse(saved));
        } catch (Exception e) {
            return Result.error("创建用户失败：" + e.getMessage());
        }
    }

    // ====================== 查询所有用户 ======================
    @GetMapping("/list")
    public Result<List<UserResponse>> getAllUsers() {
        List<User> list = userService.getAllUsers();
        List<UserResponse> responses = list.stream()
                .map(UserResponse::tResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    // ====================== 根据ID查询 ======================
    @GetMapping("/{id}")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> Result.success(UserResponse.tResponse(u)))
                .orElseGet(() -> Result.error("用户不存在"));
    }

    // ====================== 根据用户名查询 ======================
    @GetMapping("/username/{username}")
    public Result<UserResponse> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(u -> Result.success(UserResponse.tResponse(u)))
                .orElseGet(() -> Result.error("用户不存在"));
    }

    // ====================== 安全更新用户（不覆盖敏感字段）======================
    @PutMapping("/update/{id}")
    public Result<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserCreateDTO dto) {

        User update = userService.updateUserInfo(id, dto);
        return Result.success(UserResponse.tResponse(update));
    }

    // ====================== 删除用户 ======================
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    // ====================== 启用/禁用 ======================
    @PutMapping("/toggle/{id}")
    public Result<UserResponse> toggleStatus(@PathVariable Long id) {
        User user = userService.toggleUserStatus(id);
        return Result.success(UserResponse.tResponse(user));
    }

    // ====================== 修改密码 ======================
    @PostMapping("/change-pwd/{id}")
    public Result<?> changePwd(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}