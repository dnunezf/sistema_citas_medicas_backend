package com.example.sistema_citas_medicas_backend.presentacion.controllers;


import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin("*")
public class HorarioMedicoController {

    private final HorarioMedicoService horarioMedicoService;

    public HorarioMedicoController(HorarioMedicoService horarioMedicoService) {
        this.horarioMedicoService = horarioMedicoService;
    }

    // ✅ Obtener horarios por médico
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<HorarioMedicoDto>> listarHorarios(@PathVariable Long idMedico) {
        List<HorarioMedicoDto> horarios = horarioMedicoService.obtenerHorariosPorMedico(idMedico);
        return ResponseEntity.ok(horarios);
    }

    // ✅ Obtener un horario por ID (para edición)
    @GetMapping("/{idHorario}")
    public ResponseEntity<HorarioMedicoDto> obtenerHorario(@PathVariable Long idHorario) {
        HorarioMedicoDto horario = horarioMedicoService.obtenerHorarioPorId(idHorario);
        return ResponseEntity.ok(horario);
    }

    // ✅ Crear nuevo horario
    @PostMapping("/medico/{idMedico}")
    public ResponseEntity<String> guardarHorario(@PathVariable Long idMedico, @RequestBody HorarioMedicoDto horarioDto) {
        if (horarioDto.getHoraInicio() == null || horarioDto.getHoraFin() == null) {
            return ResponseEntity.badRequest().body("Las horas de inicio y fin son obligatorias.");
        }

        horarioDto.setIdMedico(idMedico);
        horarioMedicoService.guardarHorario(horarioDto);
        return ResponseEntity.ok("Horario guardado correctamente.");
    }

    // ✅ Actualizar horario existente
    @PutMapping("/{idHorario}")
    public ResponseEntity<String> actualizarHorario(@PathVariable Long idHorario, @RequestBody HorarioMedicoDto horarioDto) {
        horarioMedicoService.actualizarHorario(idHorario, horarioDto);
        return ResponseEntity.ok("Horario actualizado correctamente.");
    }

    // ✅ Eliminar horario
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long idHorario) {
        Long idMedico = horarioMedicoService.obtenerIdMedicoPorHorario(idHorario); // debe devolverlo
        horarioMedicoService.eliminarHorario(idHorario);
        return ResponseEntity.ok("Horario eliminado correctamente del médico con ID: " + idMedico);
    }
}

