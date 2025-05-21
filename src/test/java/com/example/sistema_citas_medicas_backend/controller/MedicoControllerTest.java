package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoService medicoService;

    @MockBean
    private Mapper<MedicoEntity, MedicoDto> medicoMapper;

    private MedicoEntity medico;
    private MedicoDto medicoDto;

    @BeforeEach
    void setUp() {
        medico = new MedicoEntity(1L, "Dr. Juan", "clave123", "Cardiología", 50000.0,
                "San José", 30, "Especialista en corazón",
                MedicoEntity.EstadoAprobacion.pendiente, "/uploads/fotos_perfil/juan.jpg");

        medicoDto = new MedicoDto();
        medicoDto.setId(1L);
        medicoDto.setNombre("Dr. Juan");
        medicoDto.setEspecialidad("Cardiología");
        medicoDto.setCostoConsulta(50000.0);
        medicoDto.setLocalidad("San José");
        medicoDto.setFrecuenciaCitas(30);
        medicoDto.setPresentacion("Especialista en corazón");
    }

    @Test
    void testObtenerMedicoPorIdExistente() throws Exception {
        when(medicoService.obtenerPorId(1L)).thenReturn(Optional.of(medico));
        when(medicoMapper.mapTo(medico)).thenReturn(medicoDto);

        mockMvc.perform(get("/api/medicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Juan"))
                .andExpect(jsonPath("$.especialidad").value("Cardiología"));
    }

    @Test
    void testObtenerMedicoPorIdNoExistente() throws Exception {
        when(medicoService.obtenerPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/medicos/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarMedicoConFoto() throws Exception {
        when(medicoService.obtenerPorId(1L)).thenReturn(Optional.of(medico));
        when(medicoService.actualizarMedico(any(MedicoEntity.class))).thenReturn(medico);
        when(medicoMapper.mapTo(any(MedicoEntity.class))).thenReturn(medicoDto);

        MockMultipartFile dto = new MockMultipartFile("dto", "", "application/json", new byte[0]);
        MockMultipartFile foto = new MockMultipartFile("fotoPerfil", "foto.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-content".getBytes());

        mockMvc.perform(multipart("/api/medicos/1")
                        .file(foto)
                        .param("nombre", "Dr. Juan")
                        .param("especialidad", "Cardiología")
                        .param("costoConsulta", "50000")
                        .param("localidad", "San José")
                        .param("frecuenciaCitas", "30")
                        .param("presentacion", "Especialista en corazón")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Juan"))
                .andExpect(jsonPath("$.especialidad").value("Cardiología"));
    }

    @Test
    void testActualizarMedicoNoExistente() throws Exception {
        when(medicoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(multipart("/api/medicos/99")
                        .param("nombre", "Dr. No Existe")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());
    }
}