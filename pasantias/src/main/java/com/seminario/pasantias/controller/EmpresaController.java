package com.seminario.pasantias.controller;

import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.dto.EmpresaRequest;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
	private static final int ERROR_CODE = -1;
	private static final int SUCCESS_CODE = 0;
	private static final String ES_OBLIGATORIO = " es obligatorio";

	private final EmpresaService empresaService;

	@Autowired
	public EmpresaController(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}

	@GetMapping
	public List<Empresa> getAllEmpresas() {
		return empresaService.getAllEmpresas();
	}

	@GetMapping("/{id}")
	public Empresa getEmpresaById(@PathVariable Integer id) {
		return empresaService.getEmpresaById(id);
	}

	@GetMapping("/consultarEmpresas")
	public Object consultarEmpresas(@RequestParam(required = false) String cuit, 
									@RequestParam(required = false) String nombre) {
		// Si no se proporcionan parámetros, devolver todas las empresas
		if ((cuit == null || cuit.isEmpty()) && (nombre == null || nombre.isEmpty())) {
			return empresaService.getAllEmpresas();
		}
		
		// Si se proporciona CUIT, buscar por CUIT (más específico)
		if (cuit != null && !cuit.isEmpty()) {
			Optional<Empresa> empresa = empresaService.getEmpresaByCuit(cuit);
			if (empresa.isPresent()) {
				return List.of(empresa.get());
			} else {
				return Optional.empty();
			}
		}
		
		// Si se proporciona nombre, buscar por nombre
		if (nombre != null && !nombre.isEmpty()) {
			List<Empresa> empresas = empresaService.getEmpresasByNombre(nombre);
			if (!empresas.isEmpty()) {
				return empresas;
			} else {
				return Optional.empty();
			}
		}
		
		return Optional.empty();
	}

	@PostMapping("/crearEmpresa")
	public GenericResponse crearEmpresa(@RequestBody EmpresaRequest request) {
		GenericResponse response = new GenericResponse();
		Optional<String> validationError = validateCrearEmpresaRequest(request);
		if (validationError.isPresent()) {
			return fail(response, validationError.get());
		}

		try {
			Empresa empresa = new Empresa(null, request.getNombre(), request.getCiudad(), 
				request.getCalle(), request.getNroCalle(), request.getPiso(),
				request.getDepartamento(), request.getBarrio(), request.getEmail(), 
				request.getCuit(), request.getRazonSocial(), request.getContacto(), null);
			empresaService.createEmpresaWithContactosForCurrentUser(empresa);
			success(response);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(response, e.getMessage());
		} catch (Exception e) {
			fail(response, e.getMessage());
		}
		return response;
	}

	@PostMapping
	public void createEmpresa(@RequestBody EmpresaRequest request) {
		Empresa empresa = new Empresa(null, request.getNombre(), request.getCiudad(), 
			request.getCalle(), request.getNroCalle(), request.getPiso(), 
			request.getDepartamento(), request.getBarrio(), request.getEmail(),
			request.getCuit(), request.getRazonSocial(), request.getContacto(),null);
		empresaService.createEmpresa(empresa);
	}

	@PutMapping("/{id}")
	public void updateEmpresa(@PathVariable Integer id, @RequestBody EmpresaRequest request) {
		Empresa empresa = new Empresa(id, request.getNombre(), request.getCiudad(), 
			request.getCalle(), request.getNroCalle(), request.getPiso(), 
			request.getDepartamento(), request.getBarrio(), request.getEmail(), 
			request.getCuit(), request.getRazonSocial(), request.getContacto(), null);
		empresaService.updateEmpresa(empresa);
	}

	@PostMapping("/deleteEmpresa")
	public GenericResponse deleteEmpresa(@RequestBody Map<String, String> request) {
		GenericResponse response = new GenericResponse();
		
		// Validación de campo obligatorio
		String cuit = request.get("cuit");
		if (isBlank(cuit)) {
			return fail(response, "El CUIT" + ES_OBLIGATORIO);
		}
		
		try {
			empresaService.deleteEmpresaByCuit(cuit);
			success(response);
		} catch (Exception e) {
			fail(response, e.getMessage());
		}
		return response;
	}

	@DeleteMapping("/{id}")
	public void deleteEmpresa(@PathVariable Integer id) {
		empresaService.deleteEmpresa(id);
	}

	private Optional<String> validateCrearEmpresaRequest(EmpresaRequest request) {
		if (request == null) {
			return Optional.of("Request inválido");
		}

		Optional<String> baseError = validateRequiredEmpresaFields(request);
		if (baseError.isPresent()) {
			return baseError;
		}

		return validateContactos(request);
	}

	private Optional<String> validateRequiredEmpresaFields(EmpresaRequest request) {
		if (isBlank(request.getNombre())) {
			return Optional.of("El nombre" + ES_OBLIGATORIO);
		}
		if (isBlank(request.getRazonSocial())) {
			return Optional.of("La razón social es obligatoria");
		}
		if (isBlank(request.getCuit())) {
			return Optional.of("El CUIT" + ES_OBLIGATORIO);
		}
		if (isBlank(request.getCiudad())) {
			return Optional.of("La ciudad es obligatoria");
		}
		if (isBlank(request.getCalle())) {
			return Optional.of("La calle es obligatoria");
		}
		if (request.getContacto() == null || request.getContacto().isEmpty()) {
			return Optional.of("La lista de contactos es obligatoria");
		}
		return Optional.empty();
	}

	private Optional<String> validateContactos(EmpresaRequest request) {
		for (int i = 0; i < request.getContacto().size(); i++) {
			var contacto = request.getContacto().get(i);
			int idx = i + 1;

			if (contacto == null) {
				return Optional.of("El contacto " + idx + ES_OBLIGATORIO);
			}
			if (isBlank(contacto.getNombre())) {
				return Optional.of("El nombre del contacto " + idx + ES_OBLIGATORIO);
			}
			if (isBlank(contacto.getApellido())) {
				return Optional.of("El apellido del contacto " + idx + ES_OBLIGATORIO);
			}
			if (isBlank(contacto.getEmailResponsable())) {
				return Optional.of("El email del responsable del contacto " + idx + ES_OBLIGATORIO);
			}
		}
		return Optional.empty();
	}

	private static boolean isBlank(String value) {
		return value == null || value.isEmpty();
	}

	private static GenericResponse fail(GenericResponse response, String message) {
		response.setCode(ERROR_CODE);
		response.setMessage(message);
		return response;
	}

	private static void success(GenericResponse response) {
		response.setCode(SUCCESS_CODE);
		response.setMessage(null);
	}
}
