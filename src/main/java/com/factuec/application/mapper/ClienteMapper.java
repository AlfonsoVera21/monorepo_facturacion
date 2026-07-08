package com.factuec.application.mapper;

import com.factuec.application.dto.cliente.ClienteRequest;
import com.factuec.application.dto.cliente.ClienteResponse;
import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "activo", expression = "java(request.activo() == null || request.activo())")
    ClienteEntity toEntity(ClienteRequest request);

    @Mapping(target = "empresaId", source = "empresa.id")
    ClienteResponse toResponse(ClienteEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void update(ClienteRequest request, @MappingTarget ClienteEntity entity);
}
