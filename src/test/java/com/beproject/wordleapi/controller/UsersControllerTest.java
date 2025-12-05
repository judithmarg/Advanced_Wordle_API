package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.repository.RoleRepository;
import com.beproject.wordleapi.repository.UserRepository;
import com.beproject.wordleapi.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource; 
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
    "jwt.secret=1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF"
})
class UsersIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupRoles() {
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_ADMIN);
            roleRepository.save(role);
        }
        if (roleRepository.findByName(ERole.ROLE_PLAYER).isEmpty()) {
            Role role = new Role();
            role.setName(ERole.ROLE_PLAYER);
            roleRepository.save(role);
        }
    }

    @Test
    void shouldReturn200OkAndEmailsWhenAdminRequests() throws Exception {
        createAdminUser("admin1", "admin1@test.com");
        User requestingAdmin = createAdminUser("root", "root@test.com");

        String token = jwtService.generateToken(requestingAdmin);

        mockMvc.perform(get("/admin/users/emails")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasItems("admin1@test.com", "root@test.com")));
    }

    @Test
    void shouldReturnForbiddenWhenPlayerRequests() throws Exception {
        User player = new User();
        player.setUsername("player");
        player.setEmail("player@test.com");
        player.setPassword(passwordEncoder.encode("pass"));
        player.setActive(true);
        
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_PLAYER).orElseThrow());
        player.setRoles(roles);
        userRepository.save(player);

        String token = jwtService.generateToken(player);

        mockMvc.perform(get("/admin/users/emails")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/admin/users/emails"))
                .andExpect(status().isUnauthorized());
    }

    private User createAdminUser(String username, String email) {
        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("SecurePass!"));
        admin.setActive(true);

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow());
        admin.setRoles(roles);

        return userRepository.save(admin);
    }
}