package com.example.sistema_citas_medicas_backend.presentacion.controllers;


import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paciente/citas")
@CrossOrigin("*")
public class CitaPacienteController {

    private final CitaService citaService;

    public CitaPacienteController(CitaService citaService) {
        this.citaService = citaService;
    }

    // Obtener todas las citas del paciente (ordenadas de más recientes a más antiguas)
    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<CitaDto>> obtenerCitas(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(idPaciente));
    }

    // Filtrar por estado
    @GetMapping("/{idPaciente}/estado")
    public ResponseEntity<List<CitaDto>> filtrarPorEstado(@PathVariable Long idPaciente,
                                                          @RequestParam String estado) {
        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());
        return ResponseEntity.ok(citaService.filtrarCitasPorEstadoPaciente(idPaciente, estadoEnum));
    }

    // Filtrar por nombre del médico
    @GetMapping("/{idPaciente}/medico")
    public ResponseEntity<List<CitaDto>> filtrarPorNombreMedico(@PathVariable Long idPaciente,
                                                                @RequestParam String nombre) {
        return ResponseEntity.ok(citaService.filtrarCitasPorNombreMedico(idPaciente, nombre));
    }

    // Filtrar por estado + nombre del médico
    @GetMapping("/{idPaciente}/buscar")
    public ResponseEntity<List<CitaDto>> filtrarPorEstadoYMedico(@PathVariable Long idPaciente,
                                                                 @RequestParam(required = false) String estado,
                                                                 @RequestParam(required = false) String nombreMedico) {
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

    // Obtener detalles de una cita
    @GetMapping("/detalle/{idCita}")
    public ResponseEntity<CitaDto> obtenerDetalleCita(@PathVariable Long idCita) {
        return ResponseEntity.ok(citaService.obtenerCitaPorId(idCita));
    }
}

