package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.RoleDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.beproject.wordleapi.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsersControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UsersController controller;

    private User admin1;
    private User admin2;

    @BeforeEach
    void setUp() {
        admin1 = new User();
        admin1.setEmail("admin1@test.com");

        admin2 = new User();
        admin2.setEmail("root@test.com");
    }

    @Test
    void shouldReturnEmailsWhenAdminRequests() {
        List<String> emails = List.of("admin1@test.com", "root@test.com");

        when(userService.getAdminEmails()).thenReturn(emails);

        ResponseEntity<List<String>> result = controller.getAdminEmails();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(emails, result.getBody());
    }

    @Test
    void tryForbiddenWhenPlayerRequests() {
        when(userService.getAdminEmails())
                .thenThrow(new AccessDeniedException("Forbidden"));

        assertThrows(AccessDeniedException.class, () -> controller.getAdminEmails());
    }

    @Test
    void tryUnauthorizedWhenNoTokenProvided() {
        when(userService.getAdminEmails())
                .thenThrow(new AuthenticationCredentialsNotFoundException("Unauthorized"));

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.getAdminEmails());
    }
    @Test
    void shouldAddRolesToUser() {
        UserResponseDTO admin = new UserResponseDTO(
                admin1.getId(), "", "", true, Set.of("ROLE_ADMIN")
        );

        when(userService.addRoleToUser(eq(admin1.getId()), any(RoleDTO.class)))
                .thenReturn(admin);

        ResponseEntity<UserResponseDTO> result =
                controller.addRole(admin1.getId(), new RoleDTO(ERole.ROLE_ADMIN));

        assertEquals(200, result.getStatusCode().value());
        assertEquals(admin1.getId(), result.getBody().id());
    }
}