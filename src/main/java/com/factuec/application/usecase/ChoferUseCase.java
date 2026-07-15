package com.factuec.application.usecase;

import com.factuec.application.dto.chofer.ChoferRequest;
import com.factuec.application.dto.chofer.ChoferResponse;
import com.factuec.application.mapper.ChoferMapper;
import com.factuec.infrastructure.persistence.entity.ChoferEntity;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.repository.ChoferRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChoferUseCase {
    private final ChoferRepository choferRepository;
    private final EmpresaUseCase empresaUseCase;
    private final ChoferMapper choferMapper;

    public ChoferUseCase(ChoferRepository choferRepository, EmpresaUseCase empresaUseCase, ChoferMapper choferMapper) {
        this.choferRepository = choferRepository;
        this.empresaUseCase = empresaUseCase;
        this.choferMapper = choferMapper;
    }

    @Transactional(readOnly = true)
    public List<ChoferResponse> list(UUID empresaId) {
        List<ChoferEntity> choferes = empresaId == null ? choferRepository.findAll() : choferRepository.findByEmpresaId(empresaId);
        return choferes.stream().map(choferMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ChoferResponse get(UUID id) {
        return choferMapper.toResponse(findEntity(id));
    }

    @Transactional
    public ChoferResponse create(ChoferRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        if (choferRepository.existsByEmpresaIdAndIdentificacion(request.empresaId(), request.identificacion())) {
            throw new BusinessException("Ya existe un chofer con esa identificacion para la empresa");
        }
        ChoferEntity entity = choferMapper.toEntity(request);
        entity.setEmpresa(empresa);
        return choferMapper.toResponse(choferRepository.save(entity));
    }

    @Transactional
    public ChoferResponse update(UUID id, ChoferRequest request) {
        ChoferEntity entity = findEntity(id);
        choferMapper.update(request, entity);
        if (!entity.getEmpresa().getId().equals(request.empresaId())) {
            entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        }
        return choferMapper.toResponse(choferRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        ChoferEntity entity = findEntity(id);
        entity.setActivo(false);
        choferRepository.save(entity);
    }

    public ChoferEntity findEntity(UUID id) {
        return choferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chofer no encontrado"));
    }
}
