package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.mappers.impl.MedicoMapper;
import com.example.sistema_citas_medicas_backend.servicios.impl.MedicoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class MedicoServiceImplTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private MedicoMapper medicoMapper;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private MedicoEntity medico;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        medico = new MedicoEntity(
                1L,
                "Dr. Juan",
                "clave123",
                "Cardiología",
                50000.0,
                "San José",
                30,
                "Especialista en corazón",
                MedicoEntity.EstadoAprobacion.pendiente,
                "/uploads/fotos_perfil/juan.jpg"
        );
    }

    @Test
    void testObtenerPorId() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        Optional<MedicoEntity> resultado = medicoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Dr. Juan", resultado.get().getNombre());
    }

    @Test
    void testActualizarMedico() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(medicoRepository.save(any(MedicoEntity.class))).thenReturn(medico);

        medico.setNombre("Dr. Juan Actualizado");
        MedicoEntity actualizado = medicoService.actualizarMedico(medico);

        assertEquals("Dr. Juan Actualizado", actualizado.getNombre());
        verify(medicoRepository).save(medico);
    }

    @Test
    void testObtenerTodosMedicos() {
        when(medicoRepository.findAll()).thenReturn(Collections.singletonList(medico));

        List<MedicoEntity> medicos = medicoService.obtenerTodosMedicos();

        assertEquals(1, medicos.size());
        assertEquals("Dr. Juan", medicos.get(0).getNombre());
    }

    @Test
    void testActualizarEstadoAprobacion() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        medicoService.actualizarEstadoAprobacion(1L, MedicoEntity.EstadoAprobacion.aprobado);

        assertEquals(MedicoEntity.EstadoAprobacion.aprobado, medico.getEstadoAprobacion());
        verify(medicoRepository).save(medico);
    }

    @Test
    void testBuscarPorEspecialidadYUbicacion() {
        when(medicoRepository.buscarPorEspecialidadYLocalidad("Cardiología", "San José"))
                .thenReturn(Collections.singletonList(medico));

        when(medicoMapper.mapTo(medico)).thenReturn(new MedicoDto());

        List<MedicoDto> resultados = medicoService.buscarPorEspecialidadYUbicacion("Cardiología", "San José");

        assertEquals(1, resultados.size());
        verify(medicoMapper).mapTo(medico);
    }

    @Test
    void testObtenerEspecialidades() {
        when(medicoRepository.findDistinctEspecialidades()).thenReturn(List.of("Cardiología", "Dermatología"));

        List<String> especialidades = medicoService.obtenerEspecialidades();

        assertTrue(especialidades.contains("Cardiología"));
        assertEquals(2, especialidades.size());
    }
}