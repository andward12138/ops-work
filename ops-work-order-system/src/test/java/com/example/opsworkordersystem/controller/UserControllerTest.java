package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private IUserService userService;  // Mock IUserService

    @InjectMocks
    private LegacyUserController userController;  // Inject mocks into LegacyUserController

    private MockMvc mockMvc;  // MockMvc 用于模拟 HTTP 请求

    @Test
    public void testGetUserByUsername() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(new InternalResourceViewResolver())
                .build();
                
        // Prepare mock data
        UserDto mockUser = new UserDto(1, "testUser", Role.USER, "test@example.com", "12345678");
        when(userService.getUserByUsername("testUser")).thenReturn(mockUser);

        // Perform GET request and verify the response
        mockMvc.perform(get("/legacy/users/testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Assert status is 200 OK
                .andExpect(jsonPath("$.username").value("testUser"));  // Assert username field
    }

    // Other test methods for creating users, etc.
}
