package com.example.sistema_citas_medicas_backend.service;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
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
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UsuarioEntity crearUsuario(Long id, String nombre, String clave, RolUsuario rol) {
        return new UsuarioEntity(id, nombre, clave, rol);
    }

    @Test
    void testGuardarMedico() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(10L);
        medico.setNombre("Dr. House");
        medico.setClave("diagnostico");
        medico.setRol(RolUsuario.MEDICO);

        when(passwordEncoder.encode("diagnostico")).thenReturn("hashed-diag");
        medico.setClave("hashed-diag");

        when(medicoRepository.save(any())).thenReturn(medico);

        UsuarioEntity resultado = usuarioService.save(medico);

        assertNotNull(resultado);
        assertEquals("Dr. House", resultado.getNombre());
        verify(medicoRepository).save(any());
    }

    @Test
    void testLoginCorrecto() {
        UsuarioEntity usuario = crearUsuario(3L, "Luis", "hashed", RolUsuario.PACIENTE);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave123", "hashed")).thenReturn(true);

        Optional<UsuarioEntity> loginResult = usuarioService.login(3L, "clave123");

        assertTrue(loginResult.isPresent());
        assertEquals("Luis", loginResult.get().getNombre());
    }

    @Test
    void testLoginIncorrecto() {
        UsuarioEntity usuario = crearUsuario(3L, "Luis", "hashed", RolUsuario.PACIENTE);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        Optional<UsuarioEntity> loginResult = usuarioService.login(3L, "wrong");

        assertTrue(loginResult.isEmpty());
    }

    @Test
    void testActualizarParcialUsuario() {
        UsuarioEntity original = crearUsuario(4L, "Mario", "hashed", RolUsuario.MEDICO);
        UsuarioEntity updateData = new UsuarioEntity(null, "Mario G", "nuevaClave", null);

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(original));
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hashedNueva");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        UsuarioEntity actualizado = usuarioService.partialUpdate(4L, updateData);

        assertEquals("Mario G", actualizado.getNombre());
        assertEquals("hashedNueva", actualizado.getClave());
        assertEquals(RolUsuario.MEDICO, actualizado.getRol());
    }

    @Test
    void testPartialUpdateNoSobreEscribeClaveNula() {
        UsuarioEntity original = crearUsuario(11L, "Laura", "claveOriginal", RolUsuario.PACIENTE);
        UsuarioEntity updateData = new UsuarioEntity(null, "Laura Actualizada", null, null);

        when(usuarioRepository.findById(11L)).thenReturn(Optional.of(original));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        UsuarioEntity actualizado = usuarioService.partialUpdate(11L, updateData);

        assertEquals("Laura Actualizada", actualizado.getNombre());
        assertEquals("claveOriginal", actualizado.getClave());
    }

    @Test
    void testActualizarParcialUsuarioNoExistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                usuarioService.partialUpdate(999L, new UsuarioEntity()));

        assertEquals("Usuario no encontrado!", exception.getMessage());
    }

    @Test
    void testBuscarPorIdExistente() {
        UsuarioEntity usuario = crearUsuario(2L, "Carlos", "pass", RolUsuario.MEDICO);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado = usuarioService.findById(2L);

        assertTrue(resultado.isPresent());
        assertEquals("Carlos", resultado.get().getNombre());
    }

    @Test
    void testFindOne() {
        UsuarioEntity usuario = crearUsuario(8L, "Natalia", "clave", RolUsuario.PACIENTE);
        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> result = usuarioService.findOne(8L);

        assertTrue(result.isPresent());
        assertEquals("Natalia", result.get().getNombre());
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
        UsuarioEntity usuario = crearUsuario(7L, "Lucía", "clave", RolUsuario.PACIENTE);
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        List<UsuarioEntity> usuarios = usuarioService.findAll();

        assertEquals(1, usuarios.size());
        assertEquals("Lucía", usuarios.get(0).getNombre());
    }
}
