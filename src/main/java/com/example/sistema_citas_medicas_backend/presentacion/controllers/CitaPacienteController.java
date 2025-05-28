package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;

import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paciente/citas")
@CrossOrigin("*")
public class CitaPacienteController {

    private final CitaService citaService;

    public CitaPacienteController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<CitaDto>> obtenerCitas(@PathVariable Long idPaciente) {
        if (!esPacienteAutenticado(idPaciente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(idPaciente));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/{idPaciente}/estado")
    public ResponseEntity<List<CitaDto>> filtrarPorEstado(@PathVariable Long idPaciente,
                                                          @RequestParam String estado) {
        if (!esPacienteAutenticado(idPaciente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
        return ResponseEntity.ok(citaService.filtrarCitasPorEstadoPaciente(idPaciente, estadoEnum));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/{idPaciente}/medico")
    public ResponseEntity<List<CitaDto>> filtrarPorNombreMedico(@PathVariable Long idPaciente,
                                                                @RequestParam String nombre) {
        if (!esPacienteAutenticado(idPaciente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(citaService.filtrarCitasPorNombreMedico(idPaciente, nombre));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/{idPaciente}/buscar")
    public ResponseEntity<List<CitaDto>> filtrarPorEstadoYMedico(@PathVariable Long idPaciente,
                                                                 @RequestParam(required = false) String estado,
                                                                 @RequestParam(required = false) String nombreMedico) {
        if (!esPacienteAutenticado(idPaciente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean tieneEstado = estado != null && !estado.isBlank();
        boolean tieneNombre = nombreMedico != null && !nombreMedico.isBlank();

        if (tieneEstado && tieneNombre) {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstadoYNombreMedico(idPaciente, String.valueOf(estadoEnum), nombreMedico));
        } else if (tieneEstado) {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstadoPaciente(idPaciente, estadoEnum));
        } else if (tieneNombre) {
            return ResponseEntity.ok(citaService.filtrarCitasPorNombreMedico(idPaciente, nombreMedico));
        } else {
            return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(idPaciente));
        }
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/detalle/{idCita}")
    public ResponseEntity<CitaDto> obtenerDetalleCita(@PathVariable Long idCita) {
        // Aquí podrías validar que el paciente autenticado tenga permiso sobre esta cita
        return ResponseEntity.ok(citaService.obtenerCitaPorId(idCita));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCita(@RequestBody Map<String, Object> datos) {
        try {
            Long idPaciente = Long.valueOf(datos.get("idPaciente").toString());
            Long idMedico = Long.valueOf(datos.get("idMedico").toString());
            String fechaHoraStr = datos.get("fechaHora").toString();
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr);

            if (!esPacienteAutenticado(idPaciente)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            CitaDto citaCreada = citaService.agendarCita(idPaciente, idMedico, fechaHora);
            return ResponseEntity.ok(citaCreada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al confirmar cita: " + e.getMessage()));
        }
    }

    private boolean esPacienteAutenticado(Long idPaciente) {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principalObj instanceof UsuarioPrincipal principal) {
            return principal.getUsuarioEntity().getId().equals(idPaciente);
        }
        return false;
    }
}
