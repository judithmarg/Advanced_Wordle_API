package com.beproject.wordleapi.mapper;

import com.beproject.wordleapi.domain.dto.UserRegisterDTO;
import com.beproject.wordleapi.domain.dto.UserResponseDTO;
import com.beproject.wordleapi.domain.entity.Role;
import com.beproject.wordleapi.domain.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    User toEntity(UserRegisterDTO dto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserResponseDTO toDto(User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}