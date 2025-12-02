package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.mapper.UserMapper;
import com.beproject.wordleapi.repository.RoleRepository;
import com.beproject.wordleapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegisterDTO registerDTO;
    private User userEntity;
    private Role rolePlayer;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        rolePlayer = new Role(1L, ERole.ROLE_PLAYER);
        roleAdmin = new Role(2L, ERole.ROLE_ADMIN);

        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setUsername("judith");
        userEntity.setEmail("j@test.com");
        userEntity.setPassword("encodedPass");
        userEntity.setActive(true);
        userEntity.setRoles(Set.of(rolePlayer));

        registerDTO = new UserRegisterDTO("judith", "j@test.com", "Pass1234");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userMapper.toEntity(any())).thenReturn(userEntity);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(roleRepository.findByName(ERole.ROLE_PLAYER)).thenReturn(Optional.of(rolePlayer));
        when(userRepository.save(any())).thenReturn(userEntity);
        
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "judith", "j@test.com", true, Set.of("ROLE_PLAYER"));
        when(userMapper.toDto(any())).thenReturn(responseDTO);

        UserResponseDTO result = userService.registerUser(registerDTO);

        assertNotNull(result);
        assertEquals("judith", result.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfPasswordIsWeak() {
        UserRegisterDTO weakDto = new UserRegisterDTO("judith", "j@test.com", "123"); 
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(weakDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfUserExists() {
        when(userRepository.existsByUsername(any())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDTO));
    }

    @Test
    void getAdminEmails_ShouldReturnOnlyActiveAdmins() {

        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@test.com");
        adminUser.setActive(true);
        adminUser.setRoles(Set.of(roleAdmin));

        User playerUser = new User();
        playerUser.setId(2L);
        playerUser.setEmail("player@test.com");
        playerUser.setActive(true);
        playerUser.setRoles(Set.of(rolePlayer));

        User inactiveAdmin = new User();
        inactiveAdmin.setId(3L);
        inactiveAdmin.setEmail("lazy@test.com");
        inactiveAdmin.setActive(false);
        inactiveAdmin.setRoles(Set.of(roleAdmin));

        when(userRepository.findAll()).thenReturn(List.of(adminUser, playerUser, inactiveAdmin));

        List<String> emails = userService.getAdminEmails();

        assertEquals(1, emails.size());
        assertEquals("admin@test.com", emails.get(0));
    }

    @Test
    void getAllAssignedRoles_ShouldReturnUniqueRoleNames() {
        User u1 = new User(); u1.setRoles(Set.of(roleAdmin));
        User u2 = new User(); u2.setRoles(Set.of(rolePlayer));
        User u3 = new User(); u3.setRoles(Set.of(rolePlayer));

        when(userRepository.findAll()).thenReturn(List.of(u1, u2, u3));

        Set<String> roles = userService.getAllAssignedRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_PLAYER"));
    }

    @Test
    void softDeleteUser_ShouldChangeActiveToFalse() {
        
        User activeUser = new User();
        activeUser.setId(1L);
        activeUser.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        userService.softDeleteUser(1L);

        assertFalse(activeUser.isActive()); 
        verify(userRepository).save(activeUser);
    }

    @Test
    void softDeleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.softDeleteUser(99L));
    }
}