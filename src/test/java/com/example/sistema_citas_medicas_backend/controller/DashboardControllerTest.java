package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoService medicoService;

    @MockBean
    private HorarioMedicoService horarioMedicoService;

    @MockBean
    private CitaService citaService;

    private MedicoDto medicoDto;
    private HorarioMedicoDto horarioDto;
    private CitaDto citaDto;

    @BeforeEach
    void setUp() {
        medicoDto = new MedicoDto();
        medicoDto.setId(1L);
        medicoDto.setNombre("Dr. Pérez");
        medicoDto.setEstadoAprobacion("aprobado");

        horarioDto = new HorarioMedicoDto();
        horarioDto.setIdMedico(1L);
        horarioDto.setDiaSemana("lunes");
        horarioDto.setHoraInicio("08:00");
        horarioDto.setHoraFin("12:00");
        horarioDto.setTiempoCita(30);

        citaDto = new CitaDto();
        citaDto.setId(1L);
        citaDto.setIdMedico(1L);
        citaDto.setFechaHora(LocalDateTime.now());
    }

    @Test
    void testObtenerDashboard() throws Exception {
        Mockito.when(medicoService.obtenerMedicos()).thenReturn(List.of(medicoDto));
        Mockito.when(horarioMedicoService.obtenerHorariosPorMedico(1L)).thenReturn(List.of(horarioDto));
        Mockito.when(citaService.generarTodosLosEspacios(anyLong(), Mockito.anyList()))
                .thenReturn(List.of(LocalDateTime.now().plusHours(1)));
        Mockito.when(citaService.obtenerCitasPorMedico(1L)).thenReturn(List.of(citaDto));
        Mockito.when(medicoService.obtenerEspecialidades()).thenReturn(List.of("Pediatría", "Cardiología"));

        mockMvc.perform(get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicos[0].nombre").value("Dr. Pérez"))
                .andExpect(jsonPath("$.especialidades[0]").value("Pediatría"));
    }
}