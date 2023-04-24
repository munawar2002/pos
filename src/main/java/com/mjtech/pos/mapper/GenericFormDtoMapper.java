package com.mjtech.pos.mapper;

import com.mjtech.pos.dto.GenericFromDto;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;


public class GenericFormDtoMapper {

    private final static ModelMapper modelMapper = new ModelMapper();

    public static List<GenericFromDto> mapToGenericFormDtos(List<?> objects) {
        List<GenericFromDto> list = new ArrayList<>();
        for(Object object: objects) {
            GenericFromDto dto = modelMapper.map(object, GenericFromDto.class);
            list.add(dto);
        }

        return list;
    }


}
