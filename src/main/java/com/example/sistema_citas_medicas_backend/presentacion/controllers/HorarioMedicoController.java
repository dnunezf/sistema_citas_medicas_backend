package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;

import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin("*")
public class HorarioMedicoController {

    private final HorarioMedicoService horarioMedicoService;
    private final CitaService citaService;

    public HorarioMedicoController(HorarioMedicoService horarioMedicoService, CitaService citaService) {
        this.horarioMedicoService = horarioMedicoService;
        this.citaService = citaService;
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/extendido/{idMedico}")
    public ResponseEntity<Map<String, List<String>>> obtenerHorariosExtendidos(@PathVariable Long idMedico) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<HorarioMedicoDto> horarios = horarioMedicoService.obtenerHorariosPorMedico(idMedico);
        List<LocalDateTime> espacios = citaService.generarTodosLosEspaciosExtendido(idMedico, horarios);

        Map<String, List<String>> agrupados = espacios.stream()
                .collect(Collectors.groupingBy(
                        d -> d.toLocalDate().toString(),
                        LinkedHashMap::new,
                        Collectors.mapping(LocalDateTime::toString, Collectors.toList())
                ));

        return ResponseEntity.ok(agrupados);
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<HorarioMedicoDto>> listarHorarios(@PathVariable Long idMedico) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<HorarioMedicoDto> horarios = horarioMedicoService.obtenerHorariosPorMedico(idMedico);
        return ResponseEntity.ok(horarios);
    }

    @PreAuthorize("hasRole('MEDICO')")
    @GetMapping("/{idHorario}")
    public ResponseEntity<HorarioMedicoDto> obtenerHorario(@PathVariable Long idHorario) {
        HorarioMedicoDto horario = horarioMedicoService.obtenerHorarioPorId(idHorario);
        if (!esMedicoAutenticado(horario.getIdMedico())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(horario);
    }

    @PreAuthorize("hasRole('MEDICO')")
    @PostMapping("/medico/{idMedico}")
    public ResponseEntity<String> guardarHorario(@PathVariable Long idMedico, @RequestBody HorarioMedicoDto horarioDto) {
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        horarioDto.setIdMedico(idMedico);
        horarioMedicoService.guardarHorario(horarioDto);
        return ResponseEntity.ok("Horario guardado correctamente.");
    }

    @PreAuthorize("hasRole('MEDICO')")
    @PutMapping("/{idHorario}")
    public ResponseEntity<String> actualizarHorario(@PathVariable Long idHorario, @RequestBody HorarioMedicoDto horarioDto) {
        if (!esMedicoAutenticado(horarioDto.getIdMedico())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        horarioMedicoService.actualizarHorario(idHorario, horarioDto);
        return ResponseEntity.ok("Horario actualizado correctamente.");
    }

    @PreAuthorize("hasRole('MEDICO')")
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long idHorario) {
        Long idMedico = horarioMedicoService.obtenerIdMedicoPorHorario(idHorario);
        if (!esMedicoAutenticado(idMedico)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        horarioMedicoService.eliminarHorario(idHorario);
        return ResponseEntity.ok("Horario eliminado correctamente del médico con ID: " + idMedico);
    }

    private boolean esMedicoAutenticado(Long idMedico) {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principalObj instanceof UsuarioPrincipal principal) {
            return principal.getUsuarioEntity().getId().equals(idMedico);
        }
        return false;
    }
}


