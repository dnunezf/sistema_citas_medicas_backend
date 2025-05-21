package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.HorarioMedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.HorarioMedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.impl.HorarioMedicoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HorarioMedicoServiceImplTest {

    @Mock
    private HorarioMedicoRepository horarioMedicoRepository;

    @Mock
    private MedicoRepository medicoRepository;

    private ModelMapper modelMapper;

    private HorarioMedicoServiceImpl horarioService;

    private MedicoEntity medico;
    private HorarioMedicoEntity horario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
        horarioService = new HorarioMedicoServiceImpl(horarioMedicoRepository, medicoRepository, modelMapper);

        medico = new MedicoEntity();
        medico.setId(1L);
        medico.setNombre("Dr. Juan");

        horario = new HorarioMedicoEntity(medico, HorarioMedicoEntity.DiaSemana.lunes,
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        horario.setId(10L);
    }

    @Test
    void testObtenerHorariosPorMedico() {
        when(horarioMedicoRepository.findByMedicoId(1L)).thenReturn(List.of(horario));

        List<HorarioMedicoDto> resultado = horarioService.obtenerHorariosPorMedico(1L);

        assertEquals(1, resultado.size());
        assertEquals("lunes", resultado.get(0).getDiaSemana());
    }

    @Test
    void testObtenerHorarioPorId() {
        when(horarioMedicoRepository.findById(10L)).thenReturn(Optional.of(horario));

        HorarioMedicoDto dto = horarioService.obtenerHorarioPorId(10L);

        assertEquals("lunes", dto.getDiaSemana());
    }

    @Test
    void testGuardarHorario() {
        HorarioMedicoDto dto = new HorarioMedicoDto();
        dto.setIdMedico(1L);
        dto.setDiaSemana("lunes");
        dto.setHoraInicio("08:00");
        dto.setHoraFin("12:00");
        dto.setTiempoCita(30);

        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(horarioMedicoRepository.save(any(HorarioMedicoEntity.class))).thenReturn(horario);

        HorarioMedicoEntity saved = horarioService.guardarHorario(dto);

        assertEquals(HorarioMedicoEntity.DiaSemana.lunes, saved.getDiaSemana());
        assertEquals(LocalTime.of(8, 0), saved.getHoraInicio());
    }

    @Test
    void testActualizarHorario() {
        when(horarioMedicoRepository.findById(10L)).thenReturn(Optional.of(horario));
        when(horarioMedicoRepository.save(any(HorarioMedicoEntity.class))).thenReturn(horario);

        HorarioMedicoDto dto = new HorarioMedicoDto();
        dto.setDiaSemana("martes");
        dto.setHoraInicio("09:00");
        dto.setHoraFin("13:00");
        dto.setTiempoCita(45);

        HorarioMedicoDto actualizado = horarioService.actualizarHorario(10L, dto);

        assertEquals("martes", actualizado.getDiaSemana());
    }

    @Test
    void testEliminarHorario() {
        when(horarioMedicoRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> horarioService.eliminarHorario(10L));
        verify(horarioMedicoRepository).deleteById(10L);
    }

    @Test
    void testObtenerIdMedicoPorHorario() {
        when(horarioMedicoRepository.findIdMedicoByHorario(10L)).thenReturn(1L);

        Long idMedico = horarioService.obtenerIdMedicoPorHorario(10L);

        assertEquals(1L, idMedico);
    }
}