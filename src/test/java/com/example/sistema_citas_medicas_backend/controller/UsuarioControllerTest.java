package com.example.sistema_citas_medicas_backend.controller;

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
        UsuarioDto dto = new UsuarioDto();
        dto.setId(1L);
        dto.setNombre("Juan");
        dto.setClave("1234");
        dto.setRol("PACIENTE");

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(usuarioService.save(any(UsuarioEntity.class))).thenReturn(new UsuarioEntity());

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado exitosamente."));
    }

    @Test
    void testRegistrarUsuarioExistente() throws Exception {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(1L);
        dto.setNombre("Juan");
        dto.setClave("1234");
        dto.setRol("PACIENTE");

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(new UsuarioEntity()));

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un usuario con ese ID."));
    }

    @Test
    void testRegistrarUsuarioRolInvalido() throws Exception {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(2L);
        dto.setNombre("Maria");
        dto.setClave("abcd");
        dto.setRol("INVALIDO");

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rol inválido: INVALIDO"));
    }

    @Test
    void testLoginCorrecto() throws Exception {
        UsuarioDto loginDto = new UsuarioDto();
        loginDto.setId(1L);
        loginDto.setClave("1234");

        UsuarioEntity usuario = new UsuarioEntity(1L, "Ana", "1234", RolUsuario.PACIENTE);
        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.rol").value("PACIENTE"))
                .andExpect(jsonPath("$.redirect").value("/dashboard"));
    }

    @Test
    void testLoginIncorrecto() throws Exception {
        UsuarioDto loginDto = new UsuarioDto();
        loginDto.setId(1L);
        loginDto.setClave("wrong");

        UsuarioEntity usuario = new UsuarioEntity(1L, "Ana", "1234", RolUsuario.PACIENTE);
        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas"));
    }

    @Test
    void testObtenerRoles() throws Exception {
        mockMvc.perform(get("/api/usuarios/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("MEDICO", "PACIENTE")));
    }
}