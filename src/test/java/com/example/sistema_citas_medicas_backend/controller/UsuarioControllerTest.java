package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private Mapper<UsuarioEntity, UsuarioDto> usuarioMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegistrarUsuarioNuevo() throws Exception {
        UsuarioDto dto = new UsuarioDto(1L, "Juan", "1234", "PACIENTE");
        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(usuarioService.save(any())).thenReturn(new UsuarioEntity());

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado exitosamente."));
    }

    @Test
    void testRegistrarUsuarioExistente() throws Exception {
        UsuarioDto dto = new UsuarioDto(1L, "Juan", "1234", "PACIENTE");
        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(new UsuarioEntity()));

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un usuario con ese ID."));
    }

    @Test
    void testRegistrarUsuarioRolInvalido() throws Exception {
        UsuarioDto dto = new UsuarioDto(2L, "Maria", "abcd", "INVALIDO");

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rol inválido: INVALIDO"));
    }

    @Test
    void testRegistrarUsuarioNombreVacio() throws Exception {
        UsuarioDto dto = new UsuarioDto(3L, "", "clave", "PACIENTE");

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El nombre no puede estar vacío."));
    }

    @Test
    void testLoginCorrectoPaciente() throws Exception {
        UsuarioDto loginDto = new UsuarioDto(1L, null, "1234", null);
        UsuarioEntity usuario = new UsuarioEntity(1L, "Ana", "1234", RolUsuario.PACIENTE);

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioMapper.mapTo(usuario)).thenReturn(new UsuarioDto(1L, "Ana", "1234", "PACIENTE"));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.rol").value("PACIENTE"));
    }

    @Test
    void testLoginIncorrecto() throws Exception {
        UsuarioDto loginDto = new UsuarioDto(1L, null, "wrong", null);
        UsuarioEntity usuario = new UsuarioEntity(1L, "Ana", "1234", RolUsuario.PACIENTE);

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas."));
    }

    @Test
    void testLoginMedicoPendiente() throws Exception {
        MedicoEntity medico = new MedicoEntity(2L, "Dr. Smith", "clave", "Cardiología", 100.0, "San José", 30, "Experto", MedicoEntity.EstadoAprobacion.pendiente);

        UsuarioDto loginDto = new UsuarioDto(2L, null, "clave", null);
        Mockito.when(usuarioService.findById(2L)).thenReturn(Optional.of(medico));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cuenta pendiente de aprobación por el administrador."));
    }

    @Test
    void testLoginMedicoRechazado() throws Exception {
        MedicoEntity medico = new MedicoEntity(3L, "Dr. House", "clave", "Oncología", 150.0, "Heredia", 25, "Especialista", MedicoEntity.EstadoAprobacion.rechazado);

        UsuarioDto loginDto = new UsuarioDto(3L, null, "clave", null);
        Mockito.when(usuarioService.findById(3L)).thenReturn(Optional.of(medico));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cuenta rechazada. Contacte al administrador."));
    }

    @Test
    void testLoginMedicoAprobadoPerfilIncompleto() throws Exception {
        MedicoEntity medico = new MedicoEntity(4L, "Dr. Lara", "clave", "Especialidad no definida", 80.0, "Localidad no especificada", 20, "Presentación no disponible", MedicoEntity.EstadoAprobacion.aprobado);

        UsuarioDto loginDto = new UsuarioDto(4L, null, "clave", null);
        Mockito.when(usuarioService.findById(4L)).thenReturn(Optional.of(medico));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilCompleto").value(false));
    }

    @Test
    void testLoginMedicoAprobadoPerfilCompleto() throws Exception {
        MedicoEntity medico = new MedicoEntity(5L, "Dr. Jiménez", "clave", "Pediatría", 120.0, "Cartago", 45, "Atiendo con amor", MedicoEntity.EstadoAprobacion.aprobado);

        UsuarioDto loginDto = new UsuarioDto(5L, null, "clave", null);
        Mockito.when(usuarioService.findById(5L)).thenReturn(Optional.of(medico));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilCompleto").value(true));
    }

    @Test
    void testObtenerRoles() throws Exception {
        mockMvc.perform(get("/api/usuarios/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("MEDICO", "PACIENTE")));
    }
}