package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HorarioMedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioMedicoService horarioService;

    @MockBean
    private CitaService citaService;

    private ObjectMapper objectMapper;
    private HorarioMedicoDto dto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        dto = new HorarioMedicoDto();
        dto.setId(1L);
        dto.setIdMedico(1L);
        dto.setDiaSemana("lunes");
        dto.setHoraInicio("08:00");
        dto.setHoraFin("12:00");
        dto.setTiempoCita(30);
    }

    @Test
    void testObtenerHorariosExtendidos() throws Exception {
        Mockito.when(horarioService.obtenerHorariosPorMedico(1L)).thenReturn(List.of(dto));

        List<String> dias = List.of("2025-05-26", "2025-05-27", "2025-05-28");
        List<LocalDateTime> espacios = List.of(
                LocalDateTime.of(2025, 5, 26, 8, 0),
                LocalDateTime.of(2025, 5, 27, 8, 0),
                LocalDateTime.of(2025, 5, 28, 8, 0)
        );

        Mockito.when(citaService.generarTodosLosEspaciosExtendido(eq(1L), anyList()))
                .thenReturn(espacios);

        mockMvc.perform(get("/api/horarios/extendido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['2025-05-26']").isArray())
                .andExpect(jsonPath("$.['2025-05-27']").isArray())
                .andExpect(jsonPath("$.['2025-05-28']").isArray());
    }


    @Test
    void testListarHorarios() throws Exception {
        Mockito.when(horarioService.obtenerHorariosPorMedico(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/horarios/medico/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diaSemana").value("lunes"));
    }

    @Test
    void testObtenerHorarioPorId() throws Exception {
        Mockito.when(horarioService.obtenerHorarioPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horaInicio").value("08:00"));
    }

    @Test
    void testGuardarHorario() throws Exception {
        mockMvc.perform(post("/api/horarios/medico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Horario guardado correctamente."));
    }

    @Test
    void testActualizarHorario() throws Exception {
        Mockito.when(horarioService.actualizarHorario(eq(1L), any(HorarioMedicoDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/horarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Horario actualizado correctamente."));
    }

    @Test
    void testEliminarHorario() throws Exception {
        Mockito.when(horarioService.obtenerIdMedicoPorHorario(1L)).thenReturn(1L);

        mockMvc.perform(delete("/api/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Horario eliminado correctamente del médico con ID: 1"));
    }
}