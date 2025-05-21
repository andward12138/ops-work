package com.example.opsworkordersystem.service.impl;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDto createUser(UserDto userDto) {
        // 检查用户名和邮箱是否已存在
        if (isUsernameExists(userDto.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userDto.getEmail() != null && isEmailExists(userDto.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 加密密码
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("未找到用户: " + username));
        return convertToDto(user);
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("未找到用户ID: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("未找到用户ID: " + id));
        
        // 更新用户信息
        if (userDto.getUsername() != null && !user.getUsername().equals(userDto.getUsername())) {
            if (isUsernameExists(userDto.getUsername())) {
                throw new IllegalArgumentException("用户名已存在");
            }
            user.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (isEmailExists(userDto.getEmail())) {
                throw new IllegalArgumentException("邮箱已存在");
            }
            user.setEmail(userDto.getEmail());
        }
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("未找到用户ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public boolean requestPasswordReset(String email) {
        // 邮件重置功能将在后续实现
        // 目前返回false表示未实现
        return false;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        // 密码重置功能将在后续实现
        // 目前返回false表示未实现
        return false;
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("未找到用户: " + username));
    }
    
    @Override
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 将User实体转换为UserDto
     * @param user 用户实体
     * @return 用户DTO
     */
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getPhone()
        );
    }
} 