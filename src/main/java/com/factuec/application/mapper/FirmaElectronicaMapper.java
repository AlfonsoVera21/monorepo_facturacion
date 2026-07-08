package com.factuec.application.mapper;

import com.factuec.application.dto.firma.FirmaElectronicaRequest;
import com.factuec.application.dto.firma.FirmaElectronicaResponse;
import com.factuec.infrastructure.persistence.entity.FirmaElectronicaEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FirmaElectronicaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    FirmaElectronicaEntity toEntity(FirmaElectronicaRequest request);

    @Mapping(target = "empresaId", source = "empresa.id")
    FirmaElectronicaResponse toResponse(FirmaElectronicaEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void update(FirmaElectronicaRequest request, @MappingTarget FirmaElectronicaEntity entity);
}
