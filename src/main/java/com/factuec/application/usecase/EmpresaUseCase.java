package com.factuec.application.usecase;

import com.factuec.application.dto.empresa.EmpresaRequest;
import com.factuec.application.dto.empresa.EmpresaResponse;
import com.factuec.application.mapper.EmpresaMapper;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.repository.EmpresaRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaUseCase {
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaUseCase(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> list() {
        return empresaRepository.findAll().stream().map(empresaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponse get(UUID id) {
        return empresaMapper.toResponse(findEntity(id));
    }

    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        empresaRepository.findByRuc(request.ruc()).ifPresent(existing -> {
            throw new BusinessException("Ya existe una empresa con el RUC indicado");
        });
        EmpresaEntity entity = empresaMapper.toEntity(request);
        return empresaMapper.toResponse(empresaRepository.save(entity));
    }

    @Transactional
    public EmpresaResponse update(UUID id, EmpresaRequest request) {
        EmpresaEntity entity = findEntity(id);
        empresaMapper.update(request, entity);
        return empresaMapper.toResponse(empresaRepository.save(entity));
    }

    public EmpresaEntity findEntity(UUID id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
    }
}
