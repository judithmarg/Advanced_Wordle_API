package com.beproject.wordleapi.controller;

import com.beproject.wordleapi.domain.dto.RoleDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.beproject.wordleapi.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UsersController {
    
    private final UserService userService;

    @GetMapping("/emails")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") 
    public ResponseEntity<List<String>> getAdminEmails() {
        return ResponseEntity.ok(userService.getAdminEmails());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDTO> addRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(userService.addRoleToUser(id, roleDTO));
    }
}