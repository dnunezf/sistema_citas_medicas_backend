package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.PacienteDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

        autenticarComoPaciente(1L); // Establece la autenticación válida
    }

    private void autenticarComoPaciente(Long idPaciente) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(idPaciente);
        dto.setRol("PACIENTE");

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(idPaciente);
        entity.setRol(RolUsuario.valueOf("PACIENTE"));

        UsuarioPrincipal principal = new UsuarioPrincipal(dto, entity);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
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
        autenticarComoPaciente(99L); // Autenticación para ID 99

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
        pacienteDto.setId(2L); // ID en DTO no coincide con el del path

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tienes permiso para modificar este perfil."));
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

    @Test
    void testActualizarPacienteConCamposOpcionalesNulos() throws Exception {
        PacienteDto parcial = new PacienteDto();
        parcial.setId(1L);
        parcial.setNombre("Carlos");

        PacienteEntity parcialEntity = new PacienteEntity();
        parcialEntity.setId(1L);
        parcialEntity.setNombre("Carlos");

        when(pacienteMapper.mapFrom(any(PacienteDto.class))).thenReturn(parcialEntity);

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parcial)))
                .andExpect(status().isOk())
                .andExpect(content().string("Paciente actualizado correctamente."));
    }
}
