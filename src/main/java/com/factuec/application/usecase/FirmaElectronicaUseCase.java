package com.factuec.application.usecase;

import com.factuec.application.dto.firma.FirmaElectronicaRequest;
import com.factuec.application.dto.firma.FirmaElectronicaResponse;
import com.factuec.application.mapper.FirmaElectronicaMapper;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.FirmaElectronicaEntity;
import com.factuec.infrastructure.persistence.repository.FirmaElectronicaRepository;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirmaElectronicaUseCase {
    private final FirmaElectronicaRepository firmaRepository;
    private final EmpresaUseCase empresaUseCase;
    private final FirmaElectronicaMapper firmaMapper;

    public FirmaElectronicaUseCase(FirmaElectronicaRepository firmaRepository,
                                   EmpresaUseCase empresaUseCase,
                                   FirmaElectronicaMapper firmaMapper) {
        this.firmaRepository = firmaRepository;
        this.empresaUseCase = empresaUseCase;
        this.firmaMapper = firmaMapper;
    }

    @Transactional
    public FirmaElectronicaResponse create(FirmaElectronicaRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        FirmaElectronicaEntity entity = firmaMapper.toEntity(request);
        entity.setEmpresa(empresa);
        return firmaMapper.toResponse(firmaRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<FirmaElectronicaResponse> findByEmpresa(UUID empresaId) {
        return firmaRepository.findByEmpresaId(empresaId).stream().map(firmaMapper::toResponse).toList();
    }

    @Transactional
    public FirmaElectronicaResponse update(UUID id, FirmaElectronicaRequest request) {
        FirmaElectronicaEntity entity = firmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Firma electronica no encontrada"));
        firmaMapper.update(request, entity);
        if (!entity.getEmpresa().getId().equals(request.empresaId())) {
            entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        }
        return firmaMapper.toResponse(firmaRepository.save(entity));
    }
}
