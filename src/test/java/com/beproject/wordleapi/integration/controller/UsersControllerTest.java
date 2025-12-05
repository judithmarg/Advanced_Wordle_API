package com.beproject.wordleapi.integration.controller;

import com.beproject.wordleapi.controller.UsersController;
import com.beproject.wordleapi.security.JwtAuthFilter;
import com.beproject.wordleapi.service.JwtService;
import com.beproject.wordleapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsersController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false) 
class UsersControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void shouldReturn200OkWhenAdminsExist() throws Exception {
        List<String> mockEmails = Arrays.asList("admin@test.com", "root@test.com");
        when(userService.getAdminEmails()).thenReturn(mockEmails);

        mockMvc.perform(get("/admin/users/emails")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value("admin@test.com"))
                .andExpect(jsonPath("$[1]").value("root@test.com"));
    }

    @Test
    void shouldReturn200OkWhenListIsEmpty() throws Exception {
        when(userService.getAdminEmails()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users/emails")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}