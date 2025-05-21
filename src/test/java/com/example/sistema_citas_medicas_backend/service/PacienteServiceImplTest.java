package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.servicios.impl.PacienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private PacienteEntity paciente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paciente = new PacienteEntity(1L, "Carlos", "clave", LocalDate.of(1995, 5, 20), "8888-8888", "San José");
    }

    @Test
    void testObtenerPorIdExistente() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteEntity resultado = pacienteService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
    }

    @Test
    void testActualizarPaciente() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteEntity actualizado = new PacienteEntity(1L, "Carlos Actualizado", "nuevaClave",
                LocalDate.of(1990, 1, 1), "9999-9999", "Heredia");

        pacienteService.actualizarPaciente(actualizado);

        verify(pacienteRepository).save(any(PacienteEntity.class));
        assertEquals("Carlos Actualizado", paciente.getNombre());
        assertEquals("nuevaClave", paciente.getClave());
    }

    @Test
    void testActualizarPacienteSinClave() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteEntity sinClave = new PacienteEntity(1L, "Carlos", "", LocalDate.of(1995, 5, 20), "8888-8888", "San José");

        pacienteService.actualizarPaciente(sinClave);

        verify(pacienteRepository).save(any(PacienteEntity.class));
        assertEquals("clave", paciente.getClave()); // no debe cambiar
    }

    @Test
    void testFindOne() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        Optional<PacienteEntity> resultado = pacienteService.findOne(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Carlos", resultado.get().getNombre());
    }
}