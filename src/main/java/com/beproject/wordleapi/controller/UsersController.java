package com.beproject.wordleapi.controller;

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
}