package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.CitaRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.HorarioMedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.servicios.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;

import java.time.LocalDateTime;
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

        // Inicialización manual para evitar NullPointerException
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
}