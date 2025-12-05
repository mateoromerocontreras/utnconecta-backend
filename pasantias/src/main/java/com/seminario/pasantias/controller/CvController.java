package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.CvDto;
import com.seminario.pasantias.entity.Cv;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.CvService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar las operaciones relacionadas con CVs (Currículum Vitae).
 * 
 * <p>Este controlador maneja las siguientes operaciones:
 * <ul>
 *   <li>Subir un nuevo CV (solo archivos PDF)</li>
 *   <li>Listar todos los CVs de un estudiante</li>
 *   <li>Descargar un CV específico por su ID</li>
 *   <li>Eliminar un CV del sistema</li>
 * </ul>
 * 
 * <p>Todas las operaciones requieren autenticación mediante JWT Bearer token
 * y están restringidas a usuarios con roles ADMINISTRADOR o ESTUDIANTE.
 * 
 * <p>El sistema solo acepta archivos PDF para garantizar la compatibilidad
 * y seguridad en el almacenamiento de documentos.
 * 
 * @author Sistema de Pasantías
 * @version 1.0
 * @see CvService
 * @see CvDto
 * @see Cv
 */
@RestController
@RequestMapping("/cvs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*")
public class CvController {

    /** Servicio que contiene la lógica de negocio para gestión de CVs */
    @Autowired
    private CvService cvService;

    /**
     * Sube un nuevo CV al sistema asociado a un estudiante.
     * 
     * <p>Este endpoint requiere autenticación y permite a estudiantes y administradores
     * subir archivos PDF que representan sus currículums vitae. El archivo se almacena
     * en la base de datos asociado al estudiante especificado.
     * 
     * <p>Validaciones realizadas:
     * <ul>
     *   <li>El archivo debe ser de tipo PDF (application/pdf)</li>
     *   <li>El estudiante debe existir en el sistema</li>
     *   <li>El usuario autenticado debe tener permisos para realizar esta acción</li>
     * </ul>
     * 
     * @param file Archivo PDF a subir. Debe ser un MultipartFile con content-type "application/pdf"
     * @param idEstudiante ID único del estudiante al cual se asociará el CV
     * @return GenericResponse con código 0 y mensaje de éxito si la operación es exitosa,
     *         o código -1 con mensaje de error si falla la validación o la operación
     * @throws IOException si ocurre un error al leer el archivo
     * @throws IllegalArgumentException si el archivo no es PDF o el estudiante no existe
     * @see GenericResponse
     * @see MultipartFile
     */
    @PostMapping("/subirCV")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public GenericResponse subirCv(@RequestParam("file") MultipartFile file, @RequestParam("idEstudiante") Integer idEstudiante) {
        GenericResponse response = new GenericResponse();
        try {
            if (!"application/pdf".equals(file.getContentType())) {
                response.setCode(-1);
                response.setMessage("Solo se permiten archivos PDF.");
                return response;
            }
            cvService.subirCv(file, idEstudiante);
            response.setCode(0);
            response.setMessage("CV subido exitosamente.");
            return response;
        } catch (IOException e) {
            response.setCode(-1);
            response.setMessage("Error al leer el archivo: " + e.getMessage());
            return response;
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al subir el CV: " + e.getMessage());
            return response;
        }
    }

    /**
     * Obtiene la lista de todos los CVs asociados a un estudiante específico.
     * 
     * <p>Este endpoint requiere autenticación y retorna una lista de objetos CvDto
     * que contienen la información básica de cada CV (ID, nombre del archivo, fecha de subida)
     * sin incluir los datos binarios del archivo para optimizar el rendimiento.
     * 
     * <p>Los datos binarios del CV pueden obtenerse mediante el endpoint
     * {@link #descargarCv(Integer) descargarCv}.
     * 
     * @param idEstudiante ID único del estudiante del cual se desean obtener los CVs
     * @return ResponseEntity con lista de CvDto y código HTTP 200 (OK) si la operación es exitosa
     * @see CvDto
     * @see #descargarCv(Integer)
     */
    @GetMapping("/getCV")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<List<CvDto>> listarCvs(@RequestParam("idEstudiante") Integer idEstudiante) {
        List<CvDto> cvs = cvService.listarCvsPorEstudiante(idEstudiante);
        return new ResponseEntity<>(cvs, HttpStatus.OK);
    }

    /**
     * Descarga un CV específico por su ID.
     * 
     * <p>Este endpoint requiere autenticación y retorna el archivo PDF completo
     * del CV solicitado. El archivo se envía con los headers apropiados para
     * permitir su descarga en el navegador o cliente HTTP.
     * 
     * <p>La respuesta incluye:
     * <ul>
     *   <li>Content-Type: application/pdf</li>
     *   <li>Content-Disposition: attachment con el nombre del archivo original</li>
     *   <li>Body: Array de bytes con el contenido del PDF</li>
     * </ul>
     * 
     * @param id ID único del CV a descargar
     * @return ResponseEntity con código HTTP 200 (OK) y el archivo PDF en el body
     *         si el CV existe, o código HTTP 404 (NOT_FOUND) si no se encuentra
     * @see Cv
     * @see MediaType#APPLICATION_PDF
     */
    @GetMapping("/descargarCV/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<byte[]> descargarCv(@PathVariable Integer id) {
        Optional<Cv> cvOpt = cvService.descargarCv(id);
        if (cvOpt.isPresent()) {
            Cv cv = cvOpt.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cv.getNombreArchivo() + "\"")
                    .body(cv.getDatosCv());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un CV del sistema por su ID.
     * 
     * <p>Este endpoint requiere autenticación y permite eliminar permanentemente
     * un CV del sistema. La eliminación es definitiva y no puede revertirse.
     * 
     * <p>Validaciones realizadas:
     * <ul>
     *   <li>El CV debe existir en el sistema</li>
     *   <li>El usuario autenticado debe tener permisos para realizar esta acción</li>
     * </ul>
     * 
     * @param id ID único del CV a eliminar
     * @return GenericResponse con código 0 y mensaje de éxito si la operación es exitosa,
     *         o código -1 con mensaje de error si falla la operación
     * @throws IllegalArgumentException si el CV no existe
     * @see GenericResponse
     */
    @DeleteMapping("/eliminarCV/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public GenericResponse eliminarCv(@PathVariable Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            cvService.eliminarCv(id);
            response.setCode(0);
            response.setMessage("CV eliminado exitosamente.");
            return response;
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al eliminar el CV: " + e.getMessage());
            return response;
        }
    }
}
