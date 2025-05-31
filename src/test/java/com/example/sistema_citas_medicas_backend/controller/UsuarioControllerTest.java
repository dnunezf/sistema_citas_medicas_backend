package com.example.sistema_citas_medicas_backend.controller;

import com.example.sistema_citas_medicas_backend.Security.JwtUtils;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegistrarUsuarioNuevo() throws Exception {
        UsuarioDto dto = new UsuarioDto(1L, "Juan", "1234", "PACIENTE");
        when(usuarioService.findById(1L)).thenReturn(Optional.empty());
        when(usuarioService.save(any())).thenReturn(new UsuarioEntity());

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado exitosamente."));
    }

    @Test
    void testRegistrarUsuarioExistente() throws Exception {
        UsuarioDto dto = new UsuarioDto(1L, "Juan", "1234", "PACIENTE");
        when(usuarioService.findById(1L)).thenReturn(Optional.of(new UsuarioEntity()));

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
        UsuarioDto responseDto = new UsuarioDto(1L, "Ana", null, "PACIENTE");
        UsuarioEntity usuarioEntity = new UsuarioEntity(1L, "Ana", "1234", RolUsuario.PACIENTE);

        UsuarioPrincipal principal = new UsuarioPrincipal(responseDto, usuarioEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateToken(any())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.usuario.id").value(1L))
                .andExpect(jsonPath("$.usuario.nombre").value("Ana"))
                .andExpect(jsonPath("$.usuario.rol").value("PACIENTE"));
    }

    @Test
    void testObtenerRoles() throws Exception {
        mockMvc.perform(get("/api/usuarios/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("MEDICO", "PACIENTE")));
    }
}
