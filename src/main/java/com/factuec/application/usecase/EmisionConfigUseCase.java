package com.factuec.application.usecase;

import com.factuec.application.dto.empresa.EstablecimientoRequest;
import com.factuec.application.dto.empresa.EstablecimientoResponse;
import com.factuec.application.dto.empresa.PuntoEmisionRequest;
import com.factuec.application.dto.empresa.PuntoEmisionResponse;
import com.factuec.application.dto.empresa.SecuencialRequest;
import com.factuec.application.dto.empresa.SecuencialResponse;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.EstablecimientoEntity;
import com.factuec.infrastructure.persistence.entity.PuntoEmisionEntity;
import com.factuec.infrastructure.persistence.entity.SecuencialEntity;
import com.factuec.infrastructure.persistence.repository.EstablecimientoRepository;
import com.factuec.infrastructure.persistence.repository.PuntoEmisionRepository;
import com.factuec.infrastructure.persistence.repository.SecuencialRepository;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmisionConfigUseCase {
    private final EmpresaUseCase empresaUseCase;
    private final EstablecimientoRepository establecimientoRepository;
    private final PuntoEmisionRepository puntoEmisionRepository;
    private final SecuencialRepository secuencialRepository;

    public EmisionConfigUseCase(EmpresaUseCase empresaUseCase,
                                EstablecimientoRepository establecimientoRepository,
                                PuntoEmisionRepository puntoEmisionRepository,
                                SecuencialRepository secuencialRepository) {
        this.empresaUseCase = empresaUseCase;
        this.establecimientoRepository = establecimientoRepository;
        this.puntoEmisionRepository = puntoEmisionRepository;
        this.secuencialRepository = secuencialRepository;
    }

    @Transactional(readOnly = true)
    public List<EstablecimientoResponse> listEstablecimientos(UUID empresaId) {
        return establecimientoRepository.findByEmpresaId(empresaId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EstablecimientoResponse createEstablecimiento(EstablecimientoRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        EstablecimientoEntity entity = new EstablecimientoEntity();
        entity.setEmpresa(empresa);
        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setDireccion(request.direccion());
        entity.setActivo(request.activo() == null || request.activo());
        return toResponse(establecimientoRepository.save(entity));
    }

    @Transactional
    public EstablecimientoResponse updateEstablecimiento(UUID id, EstablecimientoRequest request) {
        EstablecimientoEntity entity = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setDireccion(request.direccion());
        if (request.activo() != null) {
            entity.setActivo(request.activo());
        }
        return toResponse(establecimientoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<PuntoEmisionResponse> listPuntos(UUID establecimientoId) {
        return puntoEmisionRepository.findByEstablecimientoId(establecimientoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public PuntoEmisionResponse createPunto(PuntoEmisionRequest request) {
        EstablecimientoEntity establecimiento = establecimientoRepository.findById(request.establecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        PuntoEmisionEntity entity = new PuntoEmisionEntity();
        entity.setEstablecimiento(establecimiento);
        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setActivo(request.activo() == null || request.activo());
        return toResponse(puntoEmisionRepository.save(entity));
    }

    @Transactional
    public PuntoEmisionResponse updatePunto(UUID id, PuntoEmisionRequest request) {
        PuntoEmisionEntity entity = puntoEmisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de emision no encontrado"));
        entity.setEstablecimiento(establecimientoRepository.findById(request.establecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado")));
        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        if (request.activo() != null) {
            entity.setActivo(request.activo());
        }
        return toResponse(puntoEmisionRepository.save(entity));
    }

    @Transactional
    public SecuencialResponse upsertSecuencial(SecuencialRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        EstablecimientoEntity establecimiento = establecimientoRepository.findByIdAndEmpresaId(request.establecimientoId(), request.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado para la empresa"));
        PuntoEmisionEntity punto = puntoEmisionRepository.findByIdAndEstablecimientoId(request.puntoEmisionId(), request.establecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Punto de emision no encontrado"));
        SecuencialEntity entity = secuencialRepository
                .findByEmpresaIdAndEstablecimientoIdAndPuntoEmisionIdAndTipoComprobante(
                        request.empresaId(), request.establecimientoId(), request.puntoEmisionId(), request.tipoComprobante())
                .orElseGet(SecuencialEntity::new);
        entity.setEmpresa(empresa);
        entity.setEstablecimiento(establecimiento);
        entity.setPuntoEmision(punto);
        entity.setTipoComprobante(request.tipoComprobante());
        entity.setUltimoSecuencial(request.ultimoSecuencial());
        return toResponse(secuencialRepository.save(entity));
    }

    private EstablecimientoResponse toResponse(EstablecimientoEntity entity) {
        return new EstablecimientoResponse(
                entity.getId(),
                entity.getEmpresa().getId(),
                entity.getCodigo(),
                entity.getNombre(),
                entity.getDireccion(),
                entity.isActivo());
    }

    private PuntoEmisionResponse toResponse(PuntoEmisionEntity entity) {
        return new PuntoEmisionResponse(
                entity.getId(),
                entity.getEstablecimiento().getId(),
                entity.getCodigo(),
                entity.getNombre(),
                entity.isActivo());
    }

    private SecuencialResponse toResponse(SecuencialEntity entity) {
        return new SecuencialResponse(
                entity.getId(),
                entity.getEmpresa().getId(),
                entity.getEstablecimiento().getId(),
                entity.getPuntoEmision().getId(),
                entity.getTipoComprobante(),
                entity.getUltimoSecuencial());
    }
}
