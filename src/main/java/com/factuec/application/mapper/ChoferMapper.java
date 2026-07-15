package com.factuec.application.mapper;

import com.factuec.application.dto.chofer.ChoferRequest;
import com.factuec.application.dto.chofer.ChoferResponse;
import com.factuec.infrastructure.persistence.entity.ChoferEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ChoferMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "activo", expression = "java(request.activo() == null || request.activo())")
    ChoferEntity toEntity(ChoferRequest request);

    @Mapping(target = "empresaId", source = "empresa.id")
    ChoferResponse toResponse(ChoferEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void update(ChoferRequest request, @MappingTarget ChoferEntity entity);
}
