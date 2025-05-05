package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.entity.SleepLog.MorningFeeling;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SleepLogMapper {

    @Mapping(source = "morningFeeling", target = "morningFeeling", qualifiedByName = "enumToString")
    SleepLogDto toDto(SleepLog entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "morningFeeling", target = "morningFeeling", qualifiedByName = "stringToEnum")
    SleepLog toEntity(SleepLogDto dto);

    @Named("stringToEnum")
    default MorningFeeling stringToEnum(String feeling) {
        return feeling != null ? MorningFeeling.valueOf(feeling.toUpperCase()) : null;
    }

    @Named("enumToString")
    default String enumToString(MorningFeeling feeling) {
        return feeling != null ? feeling.name() : null;
    }
}
