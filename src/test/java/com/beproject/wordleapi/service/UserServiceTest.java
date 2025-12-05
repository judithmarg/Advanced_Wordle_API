package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.mapper.UserMapper;
import com.beproject.wordleapi.repository.RoleRepository;
import com.beproject.wordleapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService service;
    
    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("judith");
        u.setEmail("j@test.com");
        u.setActive(true);
        return u;
    }

    private UserRegisterDTO registerDto(String pass) {
        return new UserRegisterDTO("judith", "j@test.com", pass);
    }

    private Role role(ERole name) {
        return new Role(1L, name);
    }

    @Test
    void registerUserShouldReturnResponseWhenDataIsValid() {
        UserRegisterDTO dto = registerDto("Pass1234");
        User userEntity = user();
        Role rolePlayer = role(ERole.ROLE_PLAYER);
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "judith", "j@test.com", true, Set.of("ROLE_PLAYER"));

        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPass");
        when(roleRepository.findByName(ERole.ROLE_PLAYER)).thenReturn(Optional.of(rolePlayer));
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(responseDTO);

        UserResponseDTO result = service.registerUser(dto);

        assertNotNull(result);
        assertEquals("judith", result.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void tryRegisterUserShouldThrowExceptionWhenPasswordIsWeak() {
        UserRegisterDTO weakDto = registerDto("123"); 

        assertThrows(IllegalArgumentException.class, () -> service.registerUser(weakDto));
        
        verify(userRepository, never()).save(any());
    }

    @Test
    void tryRegisterUserShouldThrowExceptionWhenUserExists() {
        UserRegisterDTO dto = registerDto("Pass1234");

        when(userRepository.existsByUsername(dto.username())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.registerUser(dto));
        
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAdminEmailsShouldReturnOnlyActiveAdmins() {
        Role adminRole = role(ERole.ROLE_ADMIN);
        
        User admin = user(); 
        admin.setRoles(Set.of(adminRole));
        
        User player = user(); 
        player.setEmail("p@p.com");
        player.setRoles(Set.of(role(ERole.ROLE_PLAYER)));

        when(userRepository.findAll()).thenReturn(List.of(admin, player));

        List<String> emails = service.getAdminEmails();

        assertEquals(1, emails.size());
        assertEquals("j@test.com", emails.get(0));
    }

    @Test
    void softDeleteUserShouldChangeActiveToFalse() {
        User activeUser = user();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        service.softDeleteUser(1L);

        assertFalse(activeUser.isActive()); 
        verify(userRepository).save(activeUser);
    }

    @Test
    void trySoftDeleteUserShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> service.softDeleteUser(99L));
        
        verify(userRepository, never()).save(any());
    }
}