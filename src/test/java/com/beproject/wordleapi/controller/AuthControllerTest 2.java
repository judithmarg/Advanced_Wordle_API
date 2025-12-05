package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.UserLoginDTO;
import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.security.JwtAuthFilter;
import com.beproject.wordleapi.service.JwtService;
import com.beproject.wordleapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private AuthenticationProvider authenticationProvider;

    @MockBean private JpaMetamodelMappingContext jpaMappingContext; 

    @Test
    void registerShouldReturn201WhenDataIsCorrect() throws Exception {
        UserRegisterDTO request = new UserRegisterDTO("lucia", "l@test.com", "StrongPass1");
        UserResponseDTO response = new UserResponseDTO(1L, "lucia", "l@test.com", true, Set.of("ROLE_PLAYER"));

        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("lucia"));
    }

    @Test
    void registerShouldReturn400WhenPasswordIsInvalid() throws Exception {
        UserRegisterDTO invalidRequest = new UserRegisterDTO("lucia", "l@test.com", "");

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginShouldReturn200AndTokenWhenCredentialsAreValid() throws Exception {
        UserLoginDTO loginRequest = new UserLoginDTO("lucia", "StrongPass1");
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9...";
        
        when(jwtService.generateToken(any())).thenReturn(fakeToken);

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken));
    }
}