package com.example.sistema_citas_medicas_backend.presentacion.controllers;




import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import com.example.sistema_citas_medicas_backend.servicios.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin("*")
public class CitaController {

    private final CitaService citaService;
    private final MedicoService medicoService;
    private final HorarioMedicoService horarioMedicoService;
    private final PacienteService pacienteService;

    public CitaController(CitaService citaService, MedicoService medicoService, HorarioMedicoService horarioMedicoService, PacienteService pacienteService) {
        this.citaService = citaService;
        this.medicoService = medicoService;
        this.horarioMedicoService = horarioMedicoService;
        this.pacienteService = pacienteService;
    }

    // ✅ Obtener citas de un médico
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<CitaDto>> listarCitas(@PathVariable Long idMedico) {
        List<CitaDto> citas = citaService.obtenerCitasPorMedico(idMedico);
        return ResponseEntity.ok(citas);
    }

    // ✅ Filtrar por estado
    @GetMapping("/medico/{idMedico}/estado")
    public ResponseEntity<List<CitaDto>> filtrarPorEstado(@PathVariable Long idMedico, @RequestParam String estado) {
        if (estado.equalsIgnoreCase("ALL")) {
            return listarCitas(idMedico);
        }
        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado);
        List<CitaDto> citas = citaService.filtrarCitasPorEstado(idMedico, estadoEnum);
        return ResponseEntity.ok(citas);
    }

    // ✅ Filtrar por estado y nombre paciente
    @GetMapping("/medico/{idMedico}/filtrar")
    public ResponseEntity<List<CitaDto>> filtrarCitas(
            @PathVariable Long idMedico,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nombrePaciente) {

        boolean filtraEstado = estado != null && !estado.equalsIgnoreCase("ALL");
        boolean filtraNombre = nombrePaciente != null && !nombrePaciente.isBlank();

        List<CitaDto> citas;

        if (filtraEstado && filtraNombre) {
            citas = citaService.filtrarCitasPorEstadoYNombre(idMedico,
                    CitaEntity.EstadoCita.valueOf(estado), nombrePaciente);
        } else if (filtraEstado) {
            citas = citaService.filtrarCitasPorEstado(idMedico, CitaEntity.EstadoCita.valueOf(estado));
        } else if (filtraNombre) {
            citas = citaService.filtrarCitasPorPaciente(idMedico, nombrePaciente);
        } else {
            citas = citaService.obtenerCitasPorMedico(idMedico);
        }

        return ResponseEntity.ok(citas);
    }

    // ✅ Actualizar cita
    @PutMapping("/{idCita}")
    public ResponseEntity<String> actualizarCita(
            @PathVariable Long idCita,
            @RequestBody ActualizarCitaRequest request) {
        citaService.actualizarCita(idCita, CitaEntity.EstadoCita.valueOf(request.estado()), request.notas());
        return ResponseEntity.ok("Cita actualizada");
    }

    // DTO auxiliar para actualización
    public record ActualizarCitaRequest(String estado, String notas) {}
}

