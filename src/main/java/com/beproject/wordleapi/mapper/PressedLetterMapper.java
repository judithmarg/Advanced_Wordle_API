package com.beproject.wordleapi.mapper;

import com.beproject.wordleapi.domain.dto.PressedLetterDTO;
import com.beproject.wordleapi.domain.entity.PressedLetter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PressedLetterMapper {
    PressedLetterDTO toDto(PressedLetter  pressedLetter);
    PressedLetter toEntity(PressedLetterDTO dto);
}
