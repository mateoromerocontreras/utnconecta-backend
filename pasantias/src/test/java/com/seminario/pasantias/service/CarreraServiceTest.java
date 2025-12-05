package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Carrera;
import com.seminario.pasantias.persistence.CarreraMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CarreraService.
 * These are TRUE unit tests - no Spring context, all dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class CarreraServiceTest {

    @Mock
    private CarreraMapper carreraMapper;

    @InjectMocks
    private CarreraService carreraService;

    private Carrera carrera;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
        carrera.setId(1);
        carrera.setNombre("Ingeniería en Sistemas");
    }

    @Test
    void getAllCarreras_Success() {
        // Given
        List<Carrera> expectedCarreras = Arrays.asList(carrera);
        when(carreraMapper.findAll()).thenReturn(expectedCarreras);

        // When
        List<Carrera> result = carreraService.getAllCarreras();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ingeniería en Sistemas", result.get(0).getNombre());
        verify(carreraMapper, times(1)).findAll();
    }

    @Test
    void getCarreraByNombre_Success() {
        // Given
        when(carreraMapper.findByNombre("Ingeniería en Sistemas"))
                .thenReturn(Optional.of(carrera));

        // When
        Optional<Carrera> result = carreraService.getCarreraByNombre("Ingeniería en Sistemas");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Ingeniería en Sistemas", result.get().getNombre());
        verify(carreraMapper, times(1)).findByNombre("Ingeniería en Sistemas");
    }

    @Test
    void getCarreraByNombre_NotFound() {
        // Given
        when(carreraMapper.findByNombre("No existe")).thenReturn(Optional.empty());

        // When
        Optional<Carrera> result = carreraService.getCarreraByNombre("No existe");

        // Then
        assertFalse(result.isPresent());
        verify(carreraMapper, times(1)).findByNombre("No existe");
    }

    @Test
    void createCarrera_Success() {
        // Given
        doNothing().when(carreraMapper).insert(any(Carrera.class));

        // When
        carreraService.createCarrera(carrera);

        // Then
        verify(carreraMapper, times(1)).insert(carrera);
    }

    @Test
    void updateCarrera_Success() {
        // Given
        doNothing().when(carreraMapper).update(any(Carrera.class));

        // When
        carreraService.updateCarrera(carrera);

        // Then
        verify(carreraMapper, times(1)).update(carrera);
    }

    @Test
    void deleteCarrera_Success() {
        // Given
        doNothing().when(carreraMapper).delete(anyInt());

        // When
        carreraService.deleteCarrera(1);

        // Then
        verify(carreraMapper, times(1)).delete(1);
    }

    @Test
    void deleteCarreraByNombre_Success() {
        // Given
        doNothing().when(carreraMapper).deleteByNombre(anyString());

        // When
        carreraService.deleteCarreraByNombre("Ingeniería en Sistemas");

        // Then
        verify(carreraMapper, times(1)).deleteByNombre("Ingeniería en Sistemas");
    }
}

