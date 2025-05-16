package com.example.sistema_citas_medicas_backend.presentacion.controllers;


import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // para permitir conexión desde React
public class AdministradorController {

    private final MedicoService medicoService;

    public AdministradorController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // ✅ Obtener lista de médicos
    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoEntity>> obtenerTodos() {
        List<MedicoEntity> medicos = medicoService.obtenerTodosMedicos();
        return ResponseEntity.ok(medicos);
    }

    // ✅ Cambiar estado de aprobación
    @PutMapping("/medicos/{id}/estado")
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

    // DTO interno para el cambio de estado
    public record EstadoAprobacionRequest(String estadoAprobacion) {}
}
