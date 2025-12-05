package com.beproject.wordleapi.service;

import com.beproject.wordleapi.domain.dto.RoleDTO;
import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.ERole;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;
import com.beproject.wordleapi.mapper.UserMapper;
import com.beproject.wordleapi.repository.RoleRepository;
import com.beproject.wordleapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private final Supplier<RuntimeException> roleNotFound = () -> new RuntimeException("Role not found");

    private final Consumer<User> logUserCreation = user -> 
        log.info("Usuario creado exitosamente: {}", user.getUsername());

    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        if (!isPasswordStrong.test(dto.password())) {
            throw new IllegalArgumentException("La contraseña es muy debil");
        }

        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Role role = roleRepository.findByName(ERole.ROLE_PLAYER).orElseThrow(roleNotFound);
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);
        logUserCreation.accept(savedUser);

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<String> getAdminEmails() {
        return userRepository.findAll().stream()
            .filter(User::isActive)
            .filter(user -> user.getRoles().stream()
                .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN))
            .map(User::getEmail)
            .distinct()
            .sorted(String::compareToIgnoreCase)
            .limit(10)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Set<String> getAllAssignedRoles() {
        return userRepository.findAll().stream()
            .map(User::getRoles)
            .flatMap(Set::stream)
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
    }
    
    @Transactional
    public void softDeleteUser(Long id) {
        userRepository.findById(id).ifPresentOrElse(
            user -> {
                user.setActive(false);
                userRepository.save(user);
                log.info("Soft delete aplicado a id: {}", id);
            },
            () -> { throw new RuntimeException("User not found"); }
        );
    }

    @Transactional
    public UserResponseDTO addRoleToUser(Long userId, RoleDTO roleToAdd) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleToAdd.role())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (user.getRoles().contains(role)) {
            log.warn("El usuario {} ya tiene el rol {}", user.getUsername(), roleToAdd);
            return userMapper.toDto(user);
        }

        user.getRoles().add(role);
        User updated = userRepository.save(user);

        log.info("Rol {} asignado al usuario {}", roleToAdd, updated.getUsername());

        return userMapper.toDto(updated);
    }

}