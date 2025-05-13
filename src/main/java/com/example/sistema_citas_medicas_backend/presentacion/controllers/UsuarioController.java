package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.mappers.impl.UsuarioMapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDto dto) {
        if (usuarioService.isExists(dto.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe.");
        }

        // Convertir DTO a entidad
        RolUsuario rol = RolUsuario.valueOf(dto.getRol().toUpperCase());
        UsuarioEntity usuario = new UsuarioEntity(dto.getId(), dto.getNombre(), dto.getClave(), rol);

        usuarioService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registro completado. Si es médico, debe esperar aprobación.");
    }
}