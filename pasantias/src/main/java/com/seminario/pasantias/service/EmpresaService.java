package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.Contacto;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmpresaMapper;
import com.seminario.pasantias.persistence.ContactoMapper;
import com.seminario.pasantias.security.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
	private final EmpresaMapper empresaMapper;
	private final ContactoMapper contactoMapper;
	private final SecurityService securityService;

	@Autowired
	public EmpresaService(EmpresaMapper empresaMapper, ContactoMapper contactoMapper, SecurityService securityService) {
		this.empresaMapper = empresaMapper;
		this.contactoMapper = contactoMapper;
		this.securityService = securityService;
	}

	public List<Empresa> getAllEmpresas() {
		List<Empresa> empresas = empresaMapper.findAll();
		for (Empresa empresa : empresas) {
			empresa.setContacto(contactoMapper.findByEmpresaId(empresa.getIdEmpresa()));
		}
		return empresas;
	}

	public Empresa getEmpresaById(Integer id) {
		Empresa empresa = empresaMapper.findById(id);
		if (empresa != null) {
			empresa.setContacto(contactoMapper.findByEmpresaId(id));
		}
		return empresa;
	}

	public Optional<Empresa> getEmpresaByCuit(String cuit) {
		Empresa empresa = empresaMapper.findByCuit(cuit);
		if (empresa != null) {
			empresa.setContacto(contactoMapper.findByEmpresaId(empresa.getIdEmpresa()));
			return Optional.of(empresa);
		}
		return Optional.empty();
	}

	public List<Empresa> getEmpresasByNombre(String nombre) {
		List<Empresa> empresas = empresaMapper.findByNombre(nombre);
		for (Empresa empresa : empresas) {
			empresa.setContacto(contactoMapper.findByEmpresaId(empresa.getIdEmpresa()));
		}
		return empresas;
	}

	public void createEmpresa(Empresa empresa) {
		// Set default values if not provided
		if (empresa.getActivo() == null) {
			empresa.setActivo(true);
		}
		if (empresa.getFechaCreacion() == null) {
			empresa.setFechaCreacion(LocalDateTime.now());
		}
		empresaMapper.insert(empresa);
	}

	@Transactional
	public void createEmpresaWithContactos(Empresa empresa) {
		insertEmpresaWithContactos(empresa);
	}

	/**
	 * Creates an empresa profile for the authenticated user.
	 * EMPRESA users are linked via {@code id_usuario}; administrators may create unlinked records.
	 */
	@Transactional
	public void createEmpresaWithContactosForCurrentUser(Empresa empresa) {
		Usuario usuario = securityService.getUsuarioAutenticado();

		if (securityService.esEmpresa()) {
			Integer idUsuario = usuario.getIdUsuario();
			if (empresaMapper.findByIdUsuario(idUsuario) != null) {
				throw new IllegalStateException("El usuario ya tiene una empresa registrada");
			}
			empresa.setIdUsuario(idUsuario);
		}

		if (empresa.getCuit() != null && empresaMapper.findByCuit(empresa.getCuit()) != null) {
			throw new IllegalArgumentException("Ya existe una empresa con el CUIT proporcionado");
		}

		insertEmpresaWithContactos(empresa);
	}

	private void insertEmpresaWithContactos(Empresa empresa) {
		if (empresa.getActivo() == null) {
			empresa.setActivo(true);
		}
		if (empresa.getFechaCreacion() == null) {
			empresa.setFechaCreacion(LocalDateTime.now());
		}
		empresaMapper.insert(empresa);
		if (empresa.getContacto() != null) {
			for (Contacto contacto : empresa.getContacto()) {
				contacto.setIdEmpresa(empresa.getIdEmpresa());
				contactoMapper.insert(contacto);
			}
		}
	}

	public void updateEmpresa(Empresa empresa) {
		empresaMapper.update(empresa);
	}

	public void deleteEmpresa(Integer id) {
		contactoMapper.deleteByEmpresaId(id);
		empresaMapper.delete(id);
	}

	@Transactional
	public void deleteEmpresaByCuit(String cuit) {
		// Primero obtener la empresa por CUIT para obtener el ID
		Empresa empresa = empresaMapper.findByCuit(cuit);
		if (empresa != null) {
			// Eliminar contactos primero debido a la relación foreign key
			contactoMapper.deleteByEmpresaId(empresa.getIdEmpresa());
			// Luego eliminar la empresa
			empresaMapper.deleteByCuit(cuit);
		} else {
			throw new RuntimeException("No se encontró una empresa con el CUIT proporcionado");
		}
	}
}
