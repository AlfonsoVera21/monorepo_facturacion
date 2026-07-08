package com.factuec.application.usecase;

import com.factuec.application.dto.cliente.ClienteRequest;
import com.factuec.application.dto.cliente.ClienteResponse;
import com.factuec.application.mapper.ClienteMapper;
import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.repository.ClienteRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteUseCase {
    private final ClienteRepository clienteRepository;
    private final EmpresaUseCase empresaUseCase;
    private final ClienteMapper clienteMapper;

    public ClienteUseCase(ClienteRepository clienteRepository, EmpresaUseCase empresaUseCase, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.empresaUseCase = empresaUseCase;
        this.clienteMapper = clienteMapper;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> list(UUID empresaId) {
        List<ClienteEntity> clientes = empresaId == null ? clienteRepository.findAll() : clienteRepository.findByEmpresaId(empresaId);
        return clientes.stream().map(clienteMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse get(UUID id) {
        return clienteMapper.toResponse(findEntity(id));
    }

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        if (clienteRepository.existsByEmpresaIdAndIdentificacion(request.empresaId(), request.identificacion())) {
            throw new BusinessException("Ya existe un cliente con esa identificacion para la empresa");
        }
        ClienteEntity entity = clienteMapper.toEntity(request);
        entity.setEmpresa(empresa);
        return clienteMapper.toResponse(clienteRepository.save(entity));
    }

    @Transactional
    public ClienteResponse update(UUID id, ClienteRequest request) {
        ClienteEntity entity = findEntity(id);
        clienteMapper.update(request, entity);
        if (!entity.getEmpresa().getId().equals(request.empresaId())) {
            entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        }
        return clienteMapper.toResponse(clienteRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        ClienteEntity entity = findEntity(id);
        entity.setActivo(false);
        clienteRepository.save(entity);
    }

    public ClienteEntity findEntity(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }
}
