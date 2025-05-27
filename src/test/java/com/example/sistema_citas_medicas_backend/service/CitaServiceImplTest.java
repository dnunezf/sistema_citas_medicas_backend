package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.CitaRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.HorarioMedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private HorarioMedicoRepository horarioMedicoRepository;

    private CitaServiceImpl citaService;

    private MedicoEntity medico;
    private PacienteEntity paciente;
    private CitaEntity cita;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();

        citaService = new CitaServiceImpl(
                citaRepository,
                medicoRepository,
                pacienteRepository,
                horarioMedicoRepository,
                pacienteRepository,
                modelMapper
        );

        medico = new MedicoEntity(1L, "Dr. Juan", "clave", "Cardiología", 50000.0, "San José", 30, "Reseña", MedicoEntity.EstadoAprobacion.aprobado);
        paciente = new PacienteEntity(2L, "Carlos", "clave", java.time.LocalDate.of(1990, 1, 1), "8888-8888", "San José");
        cita = new CitaEntity(paciente, medico, LocalDateTime.of(2025, 6, 10, 10, 0), CitaEntity.EstadoCita.pendiente, "Primera consulta");
        cita.setId(100L);
    }

    @Test
    void testObtenerCitasPorMedico() {
        when(citaRepository.findByMedicoOrdenadas(1L)).thenReturn(List.of(cita));

        List<CitaDto> result = citaService.obtenerCitasPorMedico(1L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getIdPaciente());
        assertEquals("Carlos", result.get(0).getNombrePaciente());
    }

    @Test
    void testActualizarCita() {
        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(CitaEntity.class))).thenReturn(cita);

        CitaDto result = citaService.actualizarCita(100L, CitaEntity.EstadoCita.confirmada, "Todo listo");

        assertEquals("confirmada", result.getEstado());
    }

    @Test
    void testObtenerCitaPorId() {
        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));

        CitaDto result = citaService.obtenerCitaPorId(100L);

        assertEquals(2L, result.getIdPaciente());
        assertEquals("Carlos", result.getNombrePaciente());
    }

    @Test
    void testAgendarCitaCorrecta() {
        LocalDateTime fecha = LocalDateTime.of(2025, 6, 15, 14, 0);

        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(citaRepository.existsByMedicoAndFechaHora(medico, fecha)).thenReturn(false);
        when(citaRepository.existsByPacienteAndFechaHora(paciente, fecha)).thenReturn(false);
        when(citaRepository.save(any(CitaEntity.class))).thenAnswer(i -> {
            CitaEntity c = (CitaEntity) i.getArguments()[0];
            c.setId(200L);
            return c;
        });

        CitaDto result = citaService.agendarCita(2L, 1L, fecha);

        assertEquals(200L, result.getId());
        assertEquals("pendiente", result.getEstado());
    }

    @Test
    void testObtenerIdMedicoPorCita() {
        when(citaRepository.findById(100L)).thenReturn(Optional.of(cita));

        Long idMedico = citaService.obtenerIdMedicoPorCita(100L);

        assertEquals(1L, idMedico);
    }

    @Test
    void testGenerarEspaciosDesdeFecha() {
        HorarioMedicoDto horario = new HorarioMedicoDto();
        horario.setDiaSemana("lunes");
        horario.setHoraInicio("08:00");
        horario.setHoraFin("09:00");
        horario.setTiempoCita(30);

        LocalDate fechaInicio = LocalDate.of(2025, 5, 26); // lunes
        when(citaRepository.existsByMedicoAndFechaHora(any(), any())).thenReturn(false);

        List<LocalDateTime> espacios = citaService.generarEspaciosDesdeFecha(1L, List.of(horario), fechaInicio, 1);

        assertEquals(2, espacios.size());
        assertEquals(LocalDateTime.of(2025, 5, 26, 8, 0), espacios.get(0));
        assertEquals(LocalDateTime.of(2025, 5, 26, 8, 30), espacios.get(1));
    }

    @Test
    void testGenerarTodosLosEspaciosExtendido() {
        HorarioMedicoDto horario = new HorarioMedicoDto();
        horario.setDiaSemana("lunes");
        horario.setHoraInicio("08:00");
        horario.setHoraFin("09:00");
        horario.setTiempoCita(30);

        when(citaRepository.existsByMedicoAndFechaHora(any(), any())).thenReturn(false);

        List<LocalDateTime> espacios = citaService.generarTodosLosEspaciosExtendido(1L, List.of(horario));

        assertFalse(espacios.isEmpty());
    }

    @Test
    void testAgendarCitaYaOcupadaPorMedico() {
        LocalDateTime fecha = LocalDateTime.of(2025, 6, 15, 14, 0);
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(citaRepository.existsByMedicoAndFechaHora(medico, fecha)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            citaService.agendarCita(2L, 1L, fecha);
        });

        assertEquals("El horario seleccionado ya está ocupado por el médico.", ex.getMessage());
    }

    @Test
    void testAgendarCitaYaOcupadaPorPaciente() {
        LocalDateTime fecha = LocalDateTime.of(2025, 6, 15, 14, 0);
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(citaRepository.existsByMedicoAndFechaHora(medico, fecha)).thenReturn(false);
        when(citaRepository.existsByPacienteAndFechaHora(paciente, fecha)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            citaService.agendarCita(2L, 1L, fecha);
        });

        assertEquals("Ya tienes una cita agendada a esa hora.", ex.getMessage());
    }

    @Test
    void testObtenerCitaPorIdNoExistente() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            citaService.obtenerCitaPorId(999L);
        });

        assertEquals("Cita no encontrada con ID: 999", ex.getMessage());
    }

    @Test
    void testObtenerIdMedicoPorCitaNoExistente() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            citaService.obtenerIdMedicoPorCita(999L);
        });

        assertEquals("Cita no encontrada", ex.getMessage());
    }

}