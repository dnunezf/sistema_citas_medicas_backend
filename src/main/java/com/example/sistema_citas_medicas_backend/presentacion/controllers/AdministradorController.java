package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministradorController {

    private final MedicoService medicoService;

    public AdministradorController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping("/medicos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<MedicoEntity>> obtenerTodos() {
        List<MedicoEntity> medicos = medicoService.obtenerTodosMedicos();
        return ResponseEntity.ok(medicos);
    }

    @PutMapping("/medicos/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> actualizarEstado(
            @PathVariable Long id,
            @RequestBody EstadoAprobacionRequest request
    ) {
        try {
            medicoService.actualizarEstadoAprobacion(id, MedicoEntity.EstadoAprobacion.valueOf(request.estadoAprobacion()));
            return ResponseEntity.ok("Estado actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el estado.");
        }
    }

    public record EstadoAprobacionRequest(String estadoAprobacion) {}
}
