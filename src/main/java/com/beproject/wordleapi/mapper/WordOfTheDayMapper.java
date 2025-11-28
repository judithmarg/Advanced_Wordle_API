package com.beproject.wordleapi.mapper;

import com.beproject.wordleapi.domain.dto.WordOfTheDayRequest;
import com.beproject.wordleapi.domain.dto.WordOfTheDayResponse;
import com.beproject.wordleapi.domain.entity.WordOfTheDay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WordOfTheDayMapper {
    WordOfTheDayResponse toDto(WordOfTheDay wordOfTheDay);
    WordOfTheDay toEntity(WordOfTheDayRequest wordOfTheDayRequest);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(WordOfTheDayRequest request, @MappingTarget WordOfTheDay wordOfTheDay);
}
