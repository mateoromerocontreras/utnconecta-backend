package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.request.*;
import com.seminario.pasantias.dto.response.*;
import com.seminario.pasantias.entity.*;
import com.seminario.pasantias.persistence.*;
import com.seminario.pasantias.util.PasantiaMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasantiaService {

    private static final String PASANTIA_NO_ENCONTRADA_PREFIX = "Pasantía no encontrada con ID: ";
    private static final Logger log = LoggerFactory.getLogger(PasantiaService.class);

    private final com.seminario.pasantias.persistence.PasantiaMapper pasantiaMapper;
    private final EmpresaMapper empresaMapper;
    private final CarreraMapper carreraMapper;
    private final PostulacionMapper postulacionMapper;
    private final PasantiaMapperUtil mapperUtil;
    private final UsuarioService usuarioService;

    @Autowired
    public PasantiaService(
            com.seminario.pasantias.persistence.PasantiaMapper pasantiaMapper,
            EmpresaMapper empresaMapper,
            CarreraMapper carreraMapper,
            PostulacionMapper postulacionMapper,
            PasantiaMapperUtil mapperUtil,
            UsuarioService usuarioService) {
        this.pasantiaMapper = pasantiaMapper;
        this.empresaMapper = empresaMapper;
        this.carreraMapper = carreraMapper;
        this.postulacionMapper = postulacionMapper;
        this.mapperUtil = mapperUtil;
        this.usuarioService = usuarioService;
    }

    /**
     * Crear una nueva pasantía.
     * La pasantía se crea directamente en estado PUBLICADA y es visible para todos los usuarios.
     */
    public PasantiaResponseDTO crearPasantia(PasantiaRequestDTO request) {
        // Validar que la empresa existe
        Empresa empresa = empresaMapper.findById(request.getIdEmpresa());
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa con ID " + request.getIdEmpresa() + " no existe");
        }
        log.debug("Empresa encontrada para pasantía. idEmpresa={}, empresa={}", request.getIdEmpresa(), empresa);

        // Validar que las carreras existen
        if (request.getIdsCarreras() != null && !request.getIdsCarreras().isEmpty()) {
            for (Integer carreraId : request.getIdsCarreras()) {
                Carrera carrera = carreraMapper.findById(carreraId);
                if (carrera == null) {
                    throw new IllegalArgumentException("La carrera con ID " + carreraId + " no existe");
                }
            }
        }

        // Validar fechas
        if (request.getFechaCaducidad() != null && request.getFechaCaducidad().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de caducidad no puede ser anterior a hoy");
        }

        // Convertir DTO a Entity
        Pasantia pasantia = mapperUtil.requestDtoToEntity(request);
        pasantia.setFechaPublicacion(LocalDate.now());
        // Las pasantías se publican directamente al crearlas
        pasantia.setEstado(EstadoPasantia.PUBLICADA);

        // Setear la empresa
        pasantia.setEmpresa(empresa);

        // Insertar en BD
        pasantiaMapper.insert(pasantia);

        // Si hay carreras, asociarlas
        if (request.getIdsCarreras() != null && !request.getIdsCarreras().isEmpty()) {
            for (Integer carreraId : request.getIdsCarreras()) {
                pasantiaMapper.insertPasantiaCarrera(pasantia.getIdPasantia(), carreraId);
            }
        }

        // Retornar con relaciones cargadas
        return mapperUtil.entityToResponseDto(
                pasantiaMapper.findByIdWithRelations(pasantia.getIdPasantia()).orElseThrow()
        );
    }

    /**
     * Actualizar una pasantía existente.
     * Solo se pueden modificar pasantías en estado PUBLICADA.
     */
    public PasantiaResponseDTO actualizarPasantia(Integer id, PasantiaRequestDTO request) {
        // Validar que la pasantía exists
        Optional<Pasantia> pasantiaOpt = pasantiaMapper.findById(id);
        if (pasantiaOpt.isEmpty()) {
            throw new IllegalArgumentException(PASANTIA_NO_ENCONTRADA_PREFIX + id);
        }
        Pasantia pasantiaExistente = pasantiaOpt.get();

        // Solo se pueden modificar pasantías publicadas
        if (pasantiaExistente.getEstado() == EstadoPasantia.FINALIZADA) {
            throw new IllegalStateException("No se puede modificar una pasantía finalizada");
        }

        // Validar que la empresa existe
        if (request.getIdEmpresa() != null) {
            Empresa empresa = empresaMapper.findById(request.getIdEmpresa());
            if (empresa == null) {
                throw new IllegalArgumentException("Empresa no encontrada");
            }
            pasantiaExistente.setEmpresa(empresa);
        }

        // Actualizar campos
        mapperUtil.updateEntityFromRequestDto(request, pasantiaExistente);
        pasantiaMapper.update(pasantiaExistente);

        // Actualizar carreras si se especificaron
        if (request.getIdsCarreras() != null) {
            // Eliminar asociaciones existentes
            pasantiaMapper.deletePasantiaCarrerasByPasantiaId(id);
            
            // Insertar nuevas asociaciones
            for (Integer carreraId : request.getIdsCarreras()) {
                Carrera carrera = carreraMapper.findById(carreraId);
                if (carrera == null) {
                    throw new IllegalArgumentException("Carrera no encontrada: " + carreraId);
                }
                pasantiaMapper.insertPasantiaCarrera(id, carreraId);
            }
        }

        // Retornar actualizada
        Optional<Pasantia> actualizadaOpt = pasantiaMapper.findByIdWithRelations(id);
        return mapperUtil.entityToResponseDto(actualizadaOpt.orElseThrow());
    }

    /**
     * Cambiar el estado de una pasantía a FINALIZADA.
     * Solo permite la transición PUBLICADA → FINALIZADA.
     */
    public PasantiaResponseDTO actualizarEstado(Integer id, ActualizarEstadoPasantiaDTO request) {
        Pasantia pasantia = pasantiaMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PASANTIA_NO_ENCONTRADA_PREFIX + id));

        // Validar transiciones de estado permitidas
        validarTransicionEstado(pasantia.getEstado(), request.getEstado());

        pasantia.setEstado(request.getEstado());
        pasantiaMapper.update(pasantia);

        return mapperUtil.entityToResponseDto(
                pasantiaMapper.findByIdWithRelations(id).orElseThrow()
        );
    }

    /**
     * Obtener pasantía por ID con toda la información
     */
    @Transactional(readOnly = true)
    public PasantiaDetalleDTO obtenerPasantiaPorId(Integer id) {
        Pasantia pasantia = pasantiaMapper.findByIdWithRelations(id)
                .orElseThrow(() -> new IllegalArgumentException(PASANTIA_NO_ENCONTRADA_PREFIX + id));

        return mapperUtil.entityToDetalleDto(pasantia);
    }

    /**
     * Buscar pasantías con filtros y paginación
     */
    @Transactional(readOnly = true)
    public PaginaDTO<PasantiaResponseDTO> buscarPasantias(PasantiaFiltroDTO filtro) {
        // Validar paginación
        if (filtro.getPagina() != null && filtro.getPagina() < 0) {
            filtro.setPagina(0);
        }
        if (filtro.getTamanio() != null && (filtro.getTamanio() < 1 || filtro.getTamanio() > 100)) {
            filtro.setTamanio(20);
        }

        // Buscar con filtros
        List<Pasantia> pasantias = pasantiaMapper.findWithFilters(filtro);
        long total = pasantiaMapper.countWithFilters(filtro);

        // Convertir a DTOs
        List<PasantiaResponseDTO> dtos = pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();

        // Crear página
        PaginaDTO<PasantiaResponseDTO> pagina = new PaginaDTO<>();
        pagina.setContenido(dtos);
        pagina.setTotalElementos(total);
        pagina.setPaginaActual(filtro.getPagina() != null ? filtro.getPagina() : 0);
        pagina.setTamanioPagina(filtro.getTamanio() != null ? filtro.getTamanio() : 20);
        pagina.setTotalPaginas((int) Math.ceil((double) total / pagina.getTamanioPagina()));

        return pagina;
    }

    /**
     * Obtener pasantías de una empresa
     */
    @Transactional(readOnly = true)
    public List<PasantiaResponseDTO> obtenerPasantiasPorEmpresa(Integer empresaId) {
        List<Pasantia> pasantias = pasantiaMapper.findByEmpresaId(empresaId);
        return pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();
    }

    /**
     * Obtener pasantías por carrera
     */
    @Transactional(readOnly = true)
    public List<PasantiaResponseDTO> obtenerPasantiasPorCarrera(Integer carreraId) {
        List<Pasantia> pasantias = pasantiaMapper.findByCarreraId(carreraId);
        return pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();
    }

    /**
     * Obtener todas las pasantías
     */
    @Transactional(readOnly = true)
    public List<PasantiaResponseDTO> obtenerTodasLasPasantias() {
        List<Pasantia> pasantias = pasantiaMapper.findAll();
        return pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();
    }

    /**
     * Obtener pasantías publicadas (visibles para todos los usuarios, disponibles para postular)
     */
    @Transactional(readOnly = true)
    public List<PasantiaResponseDTO> obtenerPasantiasPublicadas() {
        List<Pasantia> pasantias = pasantiaMapper.findByEstado("PUBLICADA");
        return pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();
    }

    /**
     * Eliminar pasantía (finalizar en lugar de dar de baja)
     */
    public void eliminarPasantia(Integer id) {
        Optional<Pasantia> pasantiaOpt = pasantiaMapper.findById(id);
        if (pasantiaOpt.isEmpty()) {
            throw new IllegalArgumentException(PASANTIA_NO_ENCONTRADA_PREFIX + id);
        }
        Pasantia pasantia = pasantiaOpt.get();

        pasantia.setEstado(EstadoPasantia.FINALIZADA);
        pasantiaMapper.update(pasantia);
    }

    /**
     * Validar transiciones de estado permitidas.
     * Solo se permite: PUBLICADA → FINALIZADA
     */
    private void validarTransicionEstado(EstadoPasantia estadoActual, EstadoPasantia nuevoEstado) {
        if (estadoActual == EstadoPasantia.PUBLICADA && nuevoEstado == EstadoPasantia.FINALIZADA) {
            return; // Transición válida
        }

        if (estadoActual == EstadoPasantia.FINALIZADA) {
            throw new IllegalStateException(
                    "No se puede cambiar el estado de una pasantía ya finalizada"
            );
        }

        throw new IllegalStateException(
                "Transición de estado no permitida: " + estadoActual + " → " + nuevoEstado
        );
    }

    /**
     * Obtener pasantías de la empresa autenticada
     */
    @Transactional(readOnly = true)
    public List<PasantiaResponseDTO> obtenerMisPasantias() {
        // Recuperar usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar el usuario en BD
        Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new SecurityException("Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();

        // Buscar la empresa asociada al usuario
        Empresa empresa = empresaMapper.findByIdUsuario(usuario.getIdUsuario());
        if (empresa == null) {
            throw new RuntimeException("Empresa no encontrada para el usuario");
        }

        // Buscar pasantías de la empresa
        List<Pasantia> pasantias = pasantiaMapper.findByEmpresaId(empresa.getIdEmpresa());
        return pasantias.stream()
                .map(mapperUtil::entityToResponseDto)
                .toList();
    }

    /**
     * Tarea programada: finaliza automáticamente las pasantías publicadas
     * cuya fecha de caducidad ha vencido.
     * Se ejecuta cada hora.
     */
    @Scheduled(fixedRate = 3600000) // Cada 1 hora (en ms)
    @Transactional
    public void finalizarPasantiasExpiradas() {
        List<Pasantia> publicadas = pasantiaMapper.findByEstado("PUBLICADA");
        LocalDate hoy = LocalDate.now();
        int count = 0;

        for (Pasantia p : publicadas) {
            if (p.getFechaCaducidad() != null && !p.getFechaCaducidad().isAfter(hoy)) {
                p.setEstado(EstadoPasantia.FINALIZADA);
                pasantiaMapper.update(p);
                count++;
                log.info("Pasantía finalizada automáticamente por caducidad. id={}, titulo={}, fechaCaducidad={}",
                        p.getIdPasantia(), p.getTitulo(), p.getFechaCaducidad());
            }
        }

        if (count > 0) {
            log.info("Tarea programada: {} pasantía(s) finalizada(s) por fecha de caducidad vencida", count);
        }
    }
}
