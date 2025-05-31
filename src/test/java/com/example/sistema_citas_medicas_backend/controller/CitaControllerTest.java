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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CitaControllerTest {

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
        citaDto.setIdMedico(101L);
        citaDto.setNombreMedico("Dr. Juan");
        citaDto.setIdPaciente(202L);
        citaDto.setNombrePaciente("Carlos");
        citaDto.setFechaHora(LocalDateTime.of(2025, 6, 10, 10, 0));
        citaDto.setEstado("pendiente");
        citaDto.setNotas("Primera consulta");
    }

    private void autenticarComoMedico(Long idMedico) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(idMedico);
        usuarioDto.setRol("MEDICO");

        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(idMedico);
        usuarioEntity.setRol(RolUsuario.valueOf("MEDICO"));

        UsuarioPrincipal principal = new UsuarioPrincipal(usuarioDto, usuarioEntity);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void testObtenerCitasPorMedico() throws Exception {
        autenticarComoMedico(101L);

        when(citaService.obtenerCitasPorMedico(101L)).thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/medico/citas/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreMedico").value("Dr. Juan"));
    }

    @Test
    void testFiltrarPorEstadoPendiente() throws Exception {
        autenticarComoMedico(101L);

        when(citaService.filtrarCitasPorEstado(101L, CitaEntity.EstadoCita.pendiente))
                .thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/medico/citas/101/estado")
                        .param("estado", "pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("pendiente"));
    }

    @Test
    void testFiltrarPorNombrePaciente() throws Exception {
        autenticarComoMedico(101L);

        when(citaService.filtrarCitasPorPaciente(101L, "Carlos")).thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/medico/citas/101/paciente")
                        .param("nombre", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombrePaciente").value("Carlos"));
    }

    @Test
    void testActualizarCita() throws Exception {
        autenticarComoMedico(101L);

        citaDto.setEstado("confirmada");

        when(citaService.actualizarCita(1L, CitaEntity.EstadoCita.confirmada, "Todo listo"))
                .thenReturn(citaDto);

        mockMvc.perform(put("/api/medico/citas/1")
                        .param("estado", "confirmada")
                        .param("notas", "Todo listo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("confirmada"));
    }

    @WithMockUser(roles = "PACIENTE")
    @Test
    void testObtenerCitaPorId() throws Exception {
        when(citaService.obtenerCitaPorId(1L)).thenReturn(citaDto);

        mockMvc.perform(get("/api/medico/citas/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testFiltrarPorEstadoYNombre() throws Exception {
        autenticarComoMedico(101L);

        when(citaService.filtrarCitasPorEstadoYNombre(101L, CitaEntity.EstadoCita.pendiente, "Carlos"))
                .thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/medico/citas/101/buscar")
                        .param("estado", "pendiente")
                        .param("nombre", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombrePaciente").value("Carlos"))
                .andExpect(jsonPath("$[0].estado").value("pendiente"));
    }

    @Test
    void testFiltrarSinParametros() throws Exception {
        autenticarComoMedico(101L);

        when(citaService.obtenerCitasPorMedico(101L)).thenReturn(List.of(citaDto));

        mockMvc.perform(get("/api/medico/citas/101/buscar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
