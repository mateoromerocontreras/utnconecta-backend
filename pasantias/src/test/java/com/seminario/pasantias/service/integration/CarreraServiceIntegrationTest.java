package com.seminario.pasantias.service.integration;

import com.seminario.pasantias.entity.Carrera;
import com.seminario.pasantias.service.CarreraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CarreraService.
 * These tests use the REAL database (H2) and REAL mappers - no mocking.
 * Tests the full integration between service and persistence layer.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CarreraServiceIntegrationTest {

    @Autowired
    private CarreraService carreraService;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        List<Carrera> carreras = carreraService.getAllCarreras();
        carreras.forEach(c -> {
            try {
                carreraService.deleteCarrera(c.getId());
            } catch (Exception e) {
                // Ignore if doesn't exist
            }
        });
    }

    @Test
    void createCarrera_IntegrationTest() {
        // Given
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");

        // When
        carreraService.createCarrera(carrera);

        // Then
        Optional<Carrera> found = carreraService.getCarreraByNombre("Ingeniería en Sistemas");
        assertTrue(found.isPresent());
        assertEquals("Ingeniería en Sistemas", found.get().getNombre());
        assertNotNull(found.get().getId());
    }

    @Test
    void getAllCarreras_IntegrationTest() {
        // Given
        Carrera carrera1 = new Carrera();
        carrera1.setNombre("Ingeniería en Sistemas");
        carreraService.createCarrera(carrera1);

        Carrera carrera2 = new Carrera();
        carrera2.setNombre("Ingeniería Industrial");
        carreraService.createCarrera(carrera2);

        // When
        List<Carrera> carreras = carreraService.getAllCarreras();

        // Then
        assertNotNull(carreras);
        assertTrue(carreras.size() >= 2);
        assertTrue(carreras.stream().anyMatch(c -> c.getNombre().equals("Ingeniería en Sistemas")));
        assertTrue(carreras.stream().anyMatch(c -> c.getNombre().equals("Ingeniería Industrial")));
    }

    @Test
    void updateCarrera_IntegrationTest() {
        // Given
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carreraService.createCarrera(carrera);

        Optional<Carrera> created = carreraService.getCarreraByNombre("Ingeniería en Sistemas");
        assertTrue(created.isPresent());
        Integer id = created.get().getId();

        // When
        Carrera updated = new Carrera();
        updated.setId(id);
        updated.setNombre("Ingeniería en Informática");
        carreraService.updateCarrera(updated);

        // Then
        Optional<Carrera> found = carreraService.getCarreraByNombre("Ingeniería en Informática");
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertEquals("Ingeniería en Informática", found.get().getNombre());
    }

    @Test
    void deleteCarrera_IntegrationTest() {
        // Given
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carreraService.createCarrera(carrera);

        Optional<Carrera> created = carreraService.getCarreraByNombre("Ingeniería en Sistemas");
        assertTrue(created.isPresent());
        Integer id = created.get().getId();

        // When
        carreraService.deleteCarrera(id);

        // Then
        Optional<Carrera> found = carreraService.getCarreraByNombre("Ingeniería en Sistemas");
        assertFalse(found.isPresent());
    }

    @Test
    void deleteCarreraByNombre_IntegrationTest() {
        // Given
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carreraService.createCarrera(carrera);

        // When
        carreraService.deleteCarreraByNombre("Ingeniería en Sistemas");

        // Then
        Optional<Carrera> found = carreraService.getCarreraByNombre("Ingeniería en Sistemas");
        assertFalse(found.isPresent());
    }
}

