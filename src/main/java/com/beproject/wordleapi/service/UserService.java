package com.beproject.wordleapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.mapper.UserMapper;
import com.beproject.wordleapi.repository.RoleRepository;
import com.beproject.wordleapi.repository.UserRepository;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final Predicate<String> isPasswordStrong = pass -> 
        pass != null && pass.length() >= 6 && pass.chars().anyMatch(Character::isDigit);

    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        if (!isPasswordStrong.test(dto.password())) {
            throw new IllegalArgumentException("La contraseña es muy débil");
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Role role = roleRepository.findByName(ERole.ROLE_PLAYER)
                .orElseThrow(() -> new RuntimeException("Error: Role PLAYER no encontrado en BD"));
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);
        log.info("Usuario creado: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<String> getAdminEmails() {
        return userRepository.findAll().stream()
            .filter(User::isActive)
            .filter(user -> user.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN))
            .map(User::getEmail)
            .collect(Collectors.toList());
    }
}