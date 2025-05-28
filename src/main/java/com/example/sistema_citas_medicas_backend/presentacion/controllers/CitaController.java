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
@RequestMapping("/api/medico/citas")
@CrossOrigin("*")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    // Validar que solo médicos puedan acceder y solo a sus citas
    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/{idMedico}")
    public ResponseEntity<List<CitaDto>> obtenerCitasPorMedico(@PathVariable Long idMedico) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(citaService.obtenerCitasPorMedico(idMedico));
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/{idMedico}/estado")
    public ResponseEntity<List<CitaDto>> filtrarPorEstado(@PathVariable Long idMedico,
                                                          @RequestParam String estado) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (estado.equalsIgnoreCase("ALL")) {
            return ResponseEntity.ok(citaService.obtenerCitasPorMedico(idMedico));
        } else {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstado(idMedico, estadoEnum));
        }
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/{idMedico}/paciente")
    public ResponseEntity<List<CitaDto>> filtrarPorPaciente(@PathVariable Long idMedico,
                                                            @RequestParam String nombre) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(citaService.filtrarCitasPorPaciente(idMedico, nombre));
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/{idMedico}/buscar")
    public ResponseEntity<List<CitaDto>> filtrarCitas(@PathVariable Long idMedico,
                                                      @RequestParam(required = false) String estado,
                                                      @RequestParam(required = false) String nombre) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean tieneEstado = estado != null && !estado.equalsIgnoreCase("ALL");
        boolean tieneNombre = nombre != null && !nombre.isBlank();

        if (tieneEstado && tieneNombre) {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstadoYNombre(idMedico, estadoEnum, nombre));
        } else if (tieneEstado) {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstado(idMedico, estadoEnum));
        } else if (tieneNombre) {
            return ResponseEntity.ok(citaService.filtrarCitasPorPaciente(idMedico, nombre));
        } else {
            return ResponseEntity.ok(citaService.obtenerCitasPorMedico(idMedico));
        }
    }

    @PreAuthorize("hasRole('MEDICO')")
    @PutMapping("/{idCita}")
    public ResponseEntity<CitaDto> actualizarCita(@PathVariable Long idCita,
                                                  @RequestParam String estado,
                                                  @RequestParam(required = false) String notas) {
        // Aquí podrías validar si el médico autenticado tiene permiso sobre la cita
        // Si tu servicio tiene método para validar eso, úsalo antes de actualizar

        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
        CitaDto actualizada = citaService.actualizarCita(idCita, estadoEnum, notas);
        return ResponseEntity.ok(actualizada);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/detalle/{idCita}")
    public ResponseEntity<CitaDto> obtenerCitaPorId(@PathVariable Long idCita) {
        // Validar que el paciente autenticado pueda ver esta cita (implementa lógica si es necesario)
        return ResponseEntity.ok(citaService.obtenerCitaPorId(idCita));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCita(@RequestBody ConfirmarCitaRequest request) {
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(request.getFechaHora());
            CitaDto cita = citaService.agendarCita(request.getIdPaciente(), request.getIdMedico(), fechaHora);
            return ResponseEntity.ok(cita);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }

    private boolean esMedicoAutenticado(Long idMedico) {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principalObj instanceof UsuarioPrincipal principal) {
            return principal.getUsuarioEntity().getId().equals(idMedico);
        }
        return false;
    }

    public static class ConfirmarCitaRequest {
        private Long idMedico;
        private Long idPaciente;
        private String fechaHora;

        public Long getIdMedico() {
            return idMedico;
        }

        public void setIdMedico(Long idMedico) {
            this.idMedico = idMedico;
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public void setIdPaciente(Long idPaciente) {
            this.idPaciente = idPaciente;
        }

        public String getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(String fechaHora) {
            this.fechaHora = fechaHora;
        }
    }
}
