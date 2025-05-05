package com.noom.interview.fullstack.sleep.mapper;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SleepLogMapper {

    SleepLogMapper INSTANCE = Mappers.getMapper(SleepLogMapper.class);

    SleepLogDto toDto(SleepLog entity);

    @Mapping(target = "id", ignore = true)
    SleepLog toEntity(SleepLogDto dto);
}
