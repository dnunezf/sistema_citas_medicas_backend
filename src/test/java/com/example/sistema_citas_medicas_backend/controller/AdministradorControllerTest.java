package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.presentacion.controllers.AdministradorController;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoService medicoService;

    private ObjectMapper objectMapper;
    private MedicoEntity medico;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        medico = new MedicoEntity(
                1L,
                "Dr. Ana",
                "clave",
                "Pediatría",
                45000.0,
                "San José",
                30,
                "Especialista en niños",
                MedicoEntity.EstadoAprobacion.pendiente
        );
    }

    @WithMockUser(roles = "ADMINISTRADOR")
    @Test
    void testObtenerTodosLosMedicos() throws Exception {
        Mockito.when(medicoService.obtenerTodosMedicos()).thenReturn(List.of(medico));

        mockMvc.perform(get("/api/admin/medicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Dr. Ana"));
    }

    @WithMockUser(roles = "ADMINISTRADOR")
    @Test
    void testActualizarEstadoAprobacionAprobado() throws Exception {
        AdministradorController.EstadoAprobacionRequest request =
                new AdministradorController.EstadoAprobacionRequest("aprobado");

        mockMvc.perform(put("/api/admin/medicos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado actualizado correctamente."));

        verify(medicoService).actualizarEstadoAprobacion(1L, MedicoEntity.EstadoAprobacion.aprobado);
    }

    @WithMockUser(roles = "ADMINISTRADOR")
    @Test
    void testActualizarEstadoAprobacionRechazado() throws Exception {
        AdministradorController.EstadoAprobacionRequest request =
                new AdministradorController.EstadoAprobacionRequest("rechazado");

        mockMvc.perform(put("/api/admin/medicos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado actualizado correctamente."));

        verify(medicoService).actualizarEstadoAprobacion(1L, MedicoEntity.EstadoAprobacion.rechazado);
    }

    @WithMockUser(roles = "ADMINISTRADOR")
    @Test
    void testActualizarEstadoAprobacionInvalido() throws Exception {
        AdministradorController.EstadoAprobacionRequest request =
                new AdministradorController.EstadoAprobacionRequest("inexistente");

        mockMvc.perform(put("/api/admin/medicos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar el estado."));
    }

    @WithMockUser(roles = "ADMINISTRADOR")
    @Test
    void testActualizarEstadoAprobacionException() throws Exception {
        AdministradorController.EstadoAprobacionRequest request =
                new AdministradorController.EstadoAprobacionRequest("rechazado");

        Mockito.doThrow(new IllegalArgumentException("ID no válido"))
                .when(medicoService)
                .actualizarEstadoAprobacion(eq(999L), any());

        mockMvc.perform(put("/api/admin/medicos/999/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar el estado."));
    }
}
