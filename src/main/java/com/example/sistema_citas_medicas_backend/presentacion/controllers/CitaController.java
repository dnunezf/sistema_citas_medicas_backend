package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Obtener todas las citas por médico, ordenadas (más recientes primero)
    @GetMapping("/{idMedico}")
    public ResponseEntity<List<CitaDto>> obtenerCitasPorMedico(@PathVariable Long idMedico) {
        return ResponseEntity.ok(citaService.obtenerCitasPorMedico(idMedico));
    }

    // Filtrar por estado
    @GetMapping("/{idMedico}/estado")
    public ResponseEntity<List<CitaDto>> filtrarPorEstado(@PathVariable Long idMedico,
                                                          @RequestParam String estado) {
        if (estado.equalsIgnoreCase("ALL")) {
            return ResponseEntity.ok(citaService.obtenerCitasPorMedico(idMedico));
        } else {
            CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
            return ResponseEntity.ok(citaService.filtrarCitasPorEstado(idMedico, estadoEnum));
        }
    }

    // Filtrar por nombre de paciente
    @GetMapping("/{idMedico}/paciente")
    public ResponseEntity<List<CitaDto>> filtrarPorPaciente(@PathVariable Long idMedico,
                                                            @RequestParam String nombre) {
        return ResponseEntity.ok(citaService.filtrarCitasPorPaciente(idMedico, nombre));
    }

    // Filtrar por estado + nombre
    @GetMapping("/{idMedico}/buscar")
    public ResponseEntity<List<CitaDto>> filtrarCitas(@PathVariable Long idMedico,
                                                      @RequestParam(required = false) String estado,
                                                      @RequestParam(required = false) String nombre) {
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

    // Completar o actualizar cita (estado + notas)
    @PutMapping("/{idCita}")
    public ResponseEntity<CitaDto> actualizarCita(@PathVariable Long idCita,
                                                  @RequestParam String estado,
                                                  @RequestParam(required = false) String notas) {
        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
        CitaDto actualizada = citaService.actualizarCita(idCita, estadoEnum, notas);
        return ResponseEntity.ok(actualizada);
    }

    // Obtener detalles de una cita
    @GetMapping("/detalle/{idCita}")
    public ResponseEntity<CitaDto> obtenerCitaPorId(@PathVariable Long idCita) {
        return ResponseEntity.ok(citaService.obtenerCitaPorId(idCita));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarCita(@RequestBody ConfirmarCitaRequest request) {
        try {
            // Validar que el paciente existe y rol es PACIENTE
            // Suponiendo que el servicio agendarCita maneja esta validación
            LocalDateTime fechaHora = LocalDateTime.parse(request.getFechaHora());
            CitaDto cita = citaService.agendarCita(request.getIdPaciente(), request.getIdMedico(), fechaHora);
            return ResponseEntity.ok(cita);
        } catch (RuntimeException ex) {
            // Retornar mensaje de error legible
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno del servidor"));
        }
    }

    public static class ConfirmarCitaRequest {
        private Long idMedico;
        private Long idPaciente;
        private String fechaHora;

        // getters y setters
        public Long getIdMedico() { return idMedico; }
        public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }
        public Long getIdPaciente() { return idPaciente; }
        public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }
        public String getFechaHora() { return fechaHora; }
        public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
    }

}