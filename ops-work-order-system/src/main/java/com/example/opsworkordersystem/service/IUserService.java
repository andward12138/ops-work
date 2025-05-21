package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    
    /**
     * 创建新用户
     * @param userDto 用户信息
     * @return 创建的用户DTO
     */
    UserDto createUser(UserDto userDto);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户DTO
     */
    UserDto getUserByUsername(String username);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户DTO
     */
    UserDto getUserById(Integer id);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDto 用户信息
     * @return 更新后的用户DTO
     */
    UserDto updateUser(Integer id, UserDto userDto);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Integer id);
    
    /**
     * 分页获取所有用户
     * @param pageable 分页信息
     * @return 用户分页
     */
    Page<UserDto> getAllUsers(Pageable pageable);
    
    /**
     * 重置密码
     * @param email 用户邮箱
     * @return 是否成功发送重置邮件
     */
    boolean requestPasswordReset(String email);
    
    /**
     * 完成密码重置
     * @param token 重置令牌
     * @param newPassword 新密码
     * @return 是否成功重置
     */
    boolean resetPassword(String token, String newPassword);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 根据用户名获取User对象（用于安全认证）
     * @param username 用户名
     * @return User对象
     */
    User loadUserByUsername(String username);
    
    /**
     * 根据角色获取用户列表
     * @param role 角色
     * @return 用户列表
     */
    List<UserDto> getUsersByRole(Role role);
} 