package com.factuec.application.mapper;

import com.factuec.application.dto.empresa.EmpresaRequest;
import com.factuec.application.dto.empresa.EmpresaResponse;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "activo", expression = "java(request.activo() == null || request.activo())")
    EmpresaEntity toEntity(EmpresaRequest request);

    EmpresaResponse toResponse(EmpresaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(EmpresaRequest request, @MappingTarget EmpresaEntity entity);
}
