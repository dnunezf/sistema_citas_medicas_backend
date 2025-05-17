package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.mappers.impl.UsuarioMapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final Mapper<UsuarioEntity, UsuarioDto> usuarioMapper;
    private final Mapper<MedicoEntity, MedicoDto> medicoMapper;

    public UsuarioController(UsuarioService usuarioService,
                             Mapper<UsuarioEntity, UsuarioDto> usuarioMapper,
                             Mapper<MedicoEntity, MedicoDto> medicoMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.medicoMapper = medicoMapper;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDto dto) {
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
    public ResponseEntity<?> login(@RequestBody UsuarioDto loginDto, HttpSession session) {
        Optional<UsuarioEntity> usuarioOpt = usuarioService.findById(loginDto.getId());

        if (usuarioOpt.isPresent() && usuarioOpt.get().getClave().equals(loginDto.getClave())) {
            UsuarioEntity usuario = usuarioOpt.get();

            // Guardar usuario en sesión
            session.setAttribute("usuario", usuario);

            // Obtener URL pendiente si existía
            String urlPendiente = (String) session.getAttribute("urlPendiente");
            session.removeAttribute("urlPendiente"); // Limpiarla después de usarla

            String rol = usuario.getRol().name();
            String redirect;

            if (urlPendiente != null) {
                redirect = urlPendiente;
            } else {
                redirect = switch (usuario.getRol()) {
                    case PACIENTE -> "/dashboard";
                    case MEDICO -> "/citas/medico/" + usuario.getId();
                    case ADMINISTRADOR -> "/admin/lista";
                };
            }

            // Armar respuesta con datos útiles
            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "rol", rol,
                    "redirect", redirect
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }



    @GetMapping("/roles")
    public ResponseEntity<List<RolUsuario>> obtenerRoles() {
        return ResponseEntity.ok(Arrays.asList(RolUsuario.MEDICO, RolUsuario.PACIENTE));
    }

}
