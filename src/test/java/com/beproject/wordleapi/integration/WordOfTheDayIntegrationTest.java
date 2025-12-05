package com.beproject.wordleapi.integration;

import com.beproject.wordleapi.controller.WordOfTheDayController;
import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.security.JwtAuthFilter;
import com.beproject.wordleapi.service.WordOfTheDayServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WordOfTheDayController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class WordOfTheDayIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private WordOfTheDayServiceImpl service;
    
    @MockBean private JpaMetamodelMappingContext jpaMappingContext;
    @MockBean private AuthenticationProvider authenticationProvider;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private com.beproject.wordleapi.service.JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllWordsShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        WordOfTheDayResponse response = new WordOfTheDayResponse(id, "PERRO", LocalDate.now());
        
        when(service.getAllWordsOfTheDays()).thenReturn(List.of(response));

        mockMvc.perform(get("/word-of-the-day")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("PERRO"));
    }

    @Test
    void getTodayWordShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        WordOfTheDayResponse response = new WordOfTheDayResponse(id, "GATOS", LocalDate.now());

        when(service.getTodayWordOfTheDay()).thenReturn(response);

        mockMvc.perform(get("/word-of-the-day/today")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("GATOS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addWordShouldReturn201() throws Exception {
        WordOfTheDayRequest request = new WordOfTheDayRequest("CASAS");
        WordOfTheDayResponse response = new WordOfTheDayResponse(UUID.randomUUID(), "CASAS", LocalDate.now());

        when(service.addWordOfTheDay(any())).thenReturn(response);

        mockMvc.perform(post("/word-of-the-day")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.word").value("CASAS"));
    }
}