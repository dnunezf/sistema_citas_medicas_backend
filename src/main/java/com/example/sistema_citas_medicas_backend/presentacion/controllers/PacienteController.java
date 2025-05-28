package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.dto.PacienteDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.PacienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin("*")
public class PacienteController {

    private final PacienteService pacienteService;
    private final Mapper<PacienteEntity, PacienteDto> pacienteMapper;

    public PacienteController(PacienteService pacienteService,
                              Mapper<PacienteEntity, PacienteDto> pacienteMapper) {
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<PacienteDto> obtenerPaciente(@PathVariable Long id) {
        UsuarioPrincipal principal = (UsuarioPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validar que el paciente solo acceda a su perfil
        if (!principal.getUsuarioEntity().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var entity = pacienteService.obtenerPorId(id);
        if (entity != null) {
            return ResponseEntity.ok(pacienteMapper.mapTo(entity));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<String> actualizarPaciente(@PathVariable Long id, @RequestBody PacienteDto pacienteDto) {
        UsuarioPrincipal principal = (UsuarioPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validar que solo el paciente autenticado pueda modificar su perfil
        if (!principal.getUsuarioEntity().getId().equals(id) || !pacienteDto.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar este perfil.");
        }

        try {
            pacienteService.actualizarPaciente(pacienteMapper.mapFrom(pacienteDto));
            return ResponseEntity.ok("Paciente actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar datos: " + e.getMessage());
        }
    }
}
