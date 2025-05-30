package com.example.sistema_citas_medicas_backend.presentacion.controllers;


import com.example.sistema_citas_medicas_backend.Security.JwtUtils;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final Mapper<UsuarioEntity, UsuarioDto> usuarioMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UsuarioController(UsuarioService usuarioService,
                             Mapper<UsuarioEntity, UsuarioDto> usuarioMapper,
                             AuthenticationManager authenticationManager,
                             JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody UsuarioDto dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío.");
        }

        if (usuarioService.findById(dto.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese ID.");
        }

        try {
            UsuarioEntity usuario;
            RolUsuario rol;

            try {
                rol = RolUsuario.valueOf(dto.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Rol inválido: " + dto.getRol());
            }

            if (rol == RolUsuario.MEDICO) {
                usuario = new MedicoEntity(
                        dto.getId(),
                        dto.getNombre(),
                        dto.getClave(),
                        "Especialidad no definida",
                        0.0,
                        "Localidad no especificada",
                        30,
                        "Presentación no disponible",
                        MedicoEntity.EstadoAprobacion.pendiente
                );
            } else if (rol == RolUsuario.PACIENTE) {
                usuario = new PacienteEntity(
                        dto.getId(),
                        dto.getNombre(),
                        dto.getClave(),
                        LocalDate.of(2000, 1, 1),
                        "000-000-0000",
                        "Dirección no especificada"
                );
            } else {
                usuario = usuarioMapper.mapFrom(dto);
            }

            usuarioService.save(usuario);
            return ResponseEntity.ok("Usuario registrado exitosamente.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDto loginDto) {
        try {
            // Convertir id a String para el AuthenticationManager (que usa loadUserByUsername)
            String idStr = String.valueOf(loginDto.getId());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(idStr, loginDto.getClave())
            );

            UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
            UsuarioEntity usuarioEntity = usuarioPrincipal.getUsuarioEntity();

            String token = jwtUtils.generateToken(usuarioPrincipal);

            UsuarioDto usuarioDto = usuarioPrincipal.getUsuario();
            usuarioDto.setClave(null);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("usuario", usuarioDto);

            if (usuarioEntity instanceof MedicoEntity medico) {
                if (medico.getEstadoAprobacion() != MedicoEntity.EstadoAprobacion.aprobado) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("mensaje", "🛑 Su cuenta aún no ha sido aprobada por un administrador."));
                }

                boolean perfilCompleto = medico.getEspecialidad() != null
                        && !medico.getEspecialidad().equalsIgnoreCase("Especialidad no definida")
                        && medico.getPresentacion() != null
                        && !medico.getPresentacion().equalsIgnoreCase("Presentación no disponible")
                        && medico.getLocalidad() != null
                        && !medico.getLocalidad().equalsIgnoreCase("Localidad no especificada");

                respuesta.put("perfilCompleto", perfilCompleto);
                respuesta.put("estadoAprobacion", medico.getEstadoAprobacion());
            }


            return ResponseEntity.ok(respuesta);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales inválidas."));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RolUsuario>> obtenerRoles() {
        return ResponseEntity.ok(Arrays.asList(RolUsuario.MEDICO, RolUsuario.PACIENTE));
    }
}
