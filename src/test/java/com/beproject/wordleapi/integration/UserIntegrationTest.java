package com.beproject.wordleapi.integration;

import com.beproject.wordleapi.controller.AuthController;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class UserIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private AuthenticationProvider authenticationProvider;
    @MockBean private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void shouldRegisterAndLoginUser_FullFlow() throws Exception {
        UserRegisterDTO registerDTO = new UserRegisterDTO("integrationUser", "integ@test.com", "Pass1234");
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "integrationUser", "integ@test.com", true, Set.of("ROLE_PLAYER"));

        when(userService.registerUser(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integrationUser"));

        UserLoginDTO loginDTO = new UserLoginDTO("integrationUser", "Pass1234");
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token-integration");

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token-integration"));
    }

    @Test
    void shouldFailLogin_WhenUserDoesNotExist() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("User1", "Pass1234");

        doThrow(new BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized()); 
    }
}