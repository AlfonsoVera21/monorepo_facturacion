package com.factuec.application.mapper;

import com.factuec.application.dto.producto.ProductoRequest;
import com.factuec.application.dto.producto.ProductoResponse;
import com.factuec.infrastructure.persistence.entity.ProductoEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "activo", expression = "java(request.activo() == null || request.activo())")
    ProductoEntity toEntity(ProductoRequest request);

    @Mapping(target = "empresaId", source = "empresa.id")
    ProductoResponse toResponse(ProductoEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void update(ProductoRequest request, @MappingTarget ProductoEntity entity);
}
