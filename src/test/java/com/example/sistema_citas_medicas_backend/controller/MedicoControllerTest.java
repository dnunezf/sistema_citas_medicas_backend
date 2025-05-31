package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
                MedicoEntity.EstadoAprobacion.aprobado, "/uploads/fotos_perfil/juan.jpg");

        medicoDto = new MedicoDto();
        medicoDto.setId(1L);
        medicoDto.setNombre("Dr. Juan");
        medicoDto.setEspecialidad("Cardiología");
        medicoDto.setCostoConsulta(50000.0);
        medicoDto.setLocalidad("San José");
        medicoDto.setFrecuenciaCitas(30);
        medicoDto.setPresentacion("Especialista en corazón");

        autenticarComoMedico(1L);  // Autenticación necesaria para PUT
    }

    private void autenticarComoMedico(Long idMedico) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(idMedico);
        dto.setRol("MEDICO");

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(idMedico);
        entity.setRol(RolUsuario.valueOf("MEDICO"));

        UsuarioPrincipal principal = new UsuarioPrincipal(dto, entity);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
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
        when(medicoService.actualizarMedico(any())).thenReturn(medico);
        when(medicoMapper.mapTo(any())).thenReturn(medicoDto);

        MockMultipartFile foto = new MockMultipartFile("fotoPerfil", "foto.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart("/api/medicos/1")
                        .file(foto)
                        .param("nombre", "Dr. Juan")
                        .param("especialidad", "Cardiología")
                        .param("costoConsulta", "50000")
                        .param("localidad", "San José")
                        .param("frecuenciaCitas", "30")
                        .param("presentacion", "Especialista en corazón")
                        .with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Juan"));
    }

    @Test
    void testActualizarMedicoSinFoto() throws Exception {
        when(medicoService.obtenerPorId(1L)).thenReturn(Optional.of(medico));
        when(medicoService.actualizarMedico(any())).thenReturn(medico);
        when(medicoMapper.mapTo(any())).thenReturn(medicoDto);

        mockMvc.perform(multipart("/api/medicos/1")
                        .param("nombre", "Dr. Actualizado")
                        .param("especialidad", "Neurología")
                        .param("costoConsulta", "60000")
                        .param("localidad", "Alajuela")
                        .param("frecuenciaCitas", "20")
                        .param("presentacion", "Neurología avanzada")
                        .with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Juan"));
    }
}
