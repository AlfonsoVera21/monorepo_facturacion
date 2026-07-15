package com.factuec.application.usecase;

import com.factuec.application.dto.producto.ProductoRequest;
import com.factuec.application.dto.producto.ProductoResponse;
import com.factuec.application.mapper.ProductoMapper;
import com.factuec.domain.enums.UnidadMedidaInventario;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.ProductoEntity;
import com.factuec.infrastructure.persistence.repository.ProductoRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoUseCase {
    private final ProductoRepository productoRepository;
    private final EmpresaUseCase empresaUseCase;
    private final ProductoMapper productoMapper;

    public ProductoUseCase(ProductoRepository productoRepository, EmpresaUseCase empresaUseCase, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.empresaUseCase = empresaUseCase;
        this.productoMapper = productoMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> list(UUID empresaId) {
        List<ProductoEntity> productos = empresaId == null ? productoRepository.findAll() : productoRepository.findByEmpresaId(empresaId);
        return productos.stream().map(productoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse get(UUID id) {
        return productoMapper.toResponse(findEntity(id));
    }

    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        if (productoRepository.existsByEmpresaIdAndCodigoPrincipal(request.empresaId(), request.codigoPrincipal())) {
            throw new BusinessException("Ya existe un producto con ese codigo para la empresa");
        }
        ProductoEntity entity = productoMapper.toEntity(request);
        entity.setEmpresa(empresa);
        applyInventoryDefaults(entity);
        return productoMapper.toResponse(productoRepository.save(entity));
    }

    @Transactional
    public ProductoResponse update(UUID id, ProductoRequest request) {
        ProductoEntity entity = findEntity(id);
        productoMapper.update(request, entity);
        if (!entity.getEmpresa().getId().equals(request.empresaId())) {
            entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        }
        applyInventoryDefaults(entity);
        return productoMapper.toResponse(productoRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        ProductoEntity entity = findEntity(id);
        entity.setActivo(false);
        productoRepository.save(entity);
    }

    public ProductoEntity findEntity(UUID id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private void applyInventoryDefaults(ProductoEntity entity) {
        if (entity.getUnidadMedida() == null) {
            entity.setUnidadMedida(UnidadMedidaInventario.UNIDAD);
        }
        if (entity.getStockMinimo() == null) {
            entity.setStockMinimo(BigDecimal.ZERO);
        }
    }
}
