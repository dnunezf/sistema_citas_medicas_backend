package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CitaPacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;

    private ObjectMapper objectMapper;
    private CitaDto citaDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        citaDto = new CitaDto();
        citaDto.setId(1L);
        citaDto.setIdPaciente(100L);
        citaDto.setIdMedico(200L);
        citaDto.setNombrePaciente("Juan");
        citaDto.setNombreMedico("Dra. Ana");
        citaDto.setEstado("pendiente");
        citaDto.setFechaHora(LocalDateTime.now());
    }

    private void autenticarComoPaciente(Long idPaciente) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(idPaciente);
        usuarioDto.setRol("PACIENTE");

        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(idPaciente);
        usuarioEntity.setRol(RolUsuario.valueOf("PACIENTE"));

        UsuarioPrincipal principal = new UsuarioPrincipal(usuarioDto, usuarioEntity);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testObtenerCitas() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.obtenerCitasPorPaciente(100L)).thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/paciente/citas/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombrePaciente").value("Juan"));
    }

    @Test
    void testFiltrarPorEstado() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.filtrarCitasPorEstadoPaciente(eq(100L), eq(CitaEntity.EstadoCita.pendiente)))
                .thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/paciente/citas/100/estado?estado=pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("pendiente"));
    }

    @Test
    void testFiltrarPorNombreMedico() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.filtrarCitasPorNombreMedico(100L, "Ana")).thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/paciente/citas/100/medico?nombre=Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreMedico").value("Dra. Ana"));
    }

    @Test
    void testFiltrarPorEstadoYMedico() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.filtrarCitasPorEstadoYNombreMedico(eq(100L), eq("pendiente"), eq("Ana")))
                .thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/paciente/citas/100/buscar?estado=pendiente&nombreMedico=Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("pendiente"))
                .andExpect(jsonPath("$[0].nombreMedico").value("Dra. Ana"));
    }

    @Test
    void testObtenerDetalleCita() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.obtenerCitaPorId(1L)).thenReturn(citaDto);

        mockMvc.perform(get("/api/paciente/citas/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testConfirmarCita() throws Exception {
        autenticarComoPaciente(100L);

        Mockito.when(citaService.agendarCita(eq(100L), eq(200L), any(LocalDateTime.class)))
                .thenReturn(citaDto);

        Map<String, Object> datos = Map.of(
                "idPaciente", 100L,
                "idMedico", 200L,
                "fechaHora", LocalDateTime.now().toString()
        );

        mockMvc.perform(post("/api/paciente/citas/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePaciente").value("Juan"));
    }

    @Test
    void testConfirmarCitaInvalida() throws Exception {
        autenticarComoPaciente(100L);

        Map<String, Object> datos = Map.of(
                "idPaciente", "invalido", // Forzar excepción de conversión
                "idMedico", 200L,
                "fechaHora", LocalDateTime.now().toString()
        );

        mockMvc.perform(post("/api/paciente/citas/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isBadRequest());
    }
}
