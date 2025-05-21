package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.UsuarioRepository;
import com.example.sistema_citas_medicas_backend.servicios.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarUsuarioNormal() {
        UsuarioEntity usuario = new UsuarioEntity(1L, "Ana", "clave123", RolUsuario.PACIENTE);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioEntity resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testBuscarPorIdExistente() {
        UsuarioEntity usuario = new UsuarioEntity(2L, "Carlos", "pass", RolUsuario.MEDICO);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado = usuarioService.findById(2L);

        assertTrue(resultado.isPresent());
        assertEquals("Carlos", resultado.get().getNombre());
        verify(usuarioRepository).findById(2L);
    }

    @Test
    void testLoginCorrecto() {
        UsuarioEntity usuario = new UsuarioEntity(3L, "Luis", "clave123", RolUsuario.PACIENTE);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> loginResult = usuarioService.login(3L, "clave123");

        assertTrue(loginResult.isPresent());
        assertEquals("Luis", loginResult.get().getNombre());
    }

    @Test
    void testLoginIncorrecto() {
        UsuarioEntity usuario = new UsuarioEntity(3L, "Luis", "clave123", RolUsuario.PACIENTE);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> loginResult = usuarioService.login(3L, "incorrecta");

        assertTrue(loginResult.isEmpty());
    }

    @Test
    void testActualizarParcialUsuario() {
        UsuarioEntity original = new UsuarioEntity(4L, "Mario", "1234", RolUsuario.MEDICO);
        UsuarioEntity updateData = new UsuarioEntity(null, "Mario G", null, null);

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(original));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        UsuarioEntity actualizado = usuarioService.partialUpdate(4L, updateData);

        assertEquals("Mario G", actualizado.getNombre());
    }

    @Test
    void testEliminarUsuario() {
        doNothing().when(usuarioRepository).deleteById(5L);

        usuarioService.delete(5L);

        verify(usuarioRepository).deleteById(5L);
    }

    @Test
    void testExisteUsuario() {
        when(usuarioRepository.existsById(6L)).thenReturn(true);

        assertTrue(usuarioService.isExists(6L));
    }

    @Test
    void testFindAll() {
        UsuarioEntity usuario = new UsuarioEntity(7L, "Lucía", "clave", RolUsuario.PACIENTE);
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        List<UsuarioEntity> usuarios = usuarioService.findAll();

        assertEquals(1, usuarios.size());
        assertEquals("Lucía", usuarios.get(0).getNombre());
    }
}