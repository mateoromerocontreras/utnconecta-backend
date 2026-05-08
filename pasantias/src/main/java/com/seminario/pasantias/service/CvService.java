package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.CvDto;
import com.seminario.pasantias.entity.Cv;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.persistence.CvMapper;
import com.seminario.pasantias.persistence.EstudianteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CvService {

    public static class EstudianteNoEncontradoException extends RuntimeException {
        public EstudianteNoEncontradoException(String message) {
            super(message);
        }
    }

    @Autowired
    private CvMapper cvMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    public void subirCv(MultipartFile file, Integer idEstudiante) throws IOException {
        Optional<Estudiante> estudianteOpt = estudianteMapper.findById(idEstudiante);
        if (estudianteOpt.isEmpty()) {
            throw new EstudianteNoEncontradoException("Estudiante no encontrado.");
        }

        Cv cv = new Cv();
        cv.setNombreArchivo(file.getOriginalFilename());
        cv.setDatosCv(file.getBytes());
        cv.setIdEstudiante(idEstudiante);

        cvMapper.insert(cv);
    }

    public List<CvDto> listarCvsPorEstudiante(Integer idEstudiante) {
        List<Cv> cvs = cvMapper.findByEstudianteId(idEstudiante);
        return cvs.stream()
                .map(cv -> new CvDto(cv.getIdCv(), cv.getNombreArchivo(), cv.getFechaSubida()))
                .toList();
    }

    public Optional<Cv> descargarCv(Integer idCv) {
        return cvMapper.findWithDataById(idCv);
    }

    public void eliminarCv(Integer idCv) {
        cvMapper.delete(idCv);
    }
}
