package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.dto.PacienteDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.PacienteService;
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

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private Mapper<PacienteEntity, PacienteDto> pacienteMapper;

    private ObjectMapper objectMapper;
    private PacienteEntity paciente;
    private PacienteDto pacienteDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        paciente = new PacienteEntity(1L, "Carlos", "clave",
                LocalDate.of(1995, 5, 20), "8888-8888", "San José");

        pacienteDto = new PacienteDto();
        pacienteDto.setId(1L);
        pacienteDto.setNombre("Carlos");
        pacienteDto.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        pacienteDto.setTelefono("8888-8888");
        pacienteDto.setDireccion("San José");
    }

    @Test
    void testObtenerPacienteExistente() throws Exception {
        when(pacienteService.obtenerPorId(1L)).thenReturn(paciente);
        when(pacienteMapper.mapTo(paciente)).thenReturn(pacienteDto);

        mockMvc.perform(get("/api/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void testObtenerPacienteNoExistente() throws Exception {
        when(pacienteService.obtenerPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarPacienteCorrectamente() throws Exception {
        when(pacienteMapper.mapFrom(any(PacienteDto.class))).thenReturn(paciente);

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Paciente actualizado correctamente."));
    }

    @Test
    void testActualizarPacienteIdNoCoincide() throws Exception {
        pacienteDto.setId(2L);

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El ID del path no coincide con el del objeto."));
    }

    @Test
    void testActualizarPacienteErrorInterno() throws Exception {
        when(pacienteMapper.mapFrom(any(PacienteDto.class))).thenReturn(paciente);
        doThrow(new RuntimeException("Error en DB")).when(pacienteService).actualizarPaciente(any(PacienteEntity.class));

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al actualizar datos: Error en DB"));
    }
}