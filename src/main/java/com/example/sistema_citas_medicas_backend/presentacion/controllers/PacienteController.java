package com.example.sistema_citas_medicas_backend.presentacion.controllers;



import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.dto.PacienteDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.PacienteService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PacienteDto> obtenerPaciente(@PathVariable Long id) {
        var entity = pacienteService.obtenerPorId(id);
        if (entity != null) {
            return ResponseEntity.ok(pacienteMapper.mapTo(entity));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPaciente(@PathVariable Long id, @RequestBody PacienteDto pacienteDto) {
        if (!pacienteDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("El ID del path no coincide con el del objeto.");
        }

        try {
            pacienteService.actualizarPaciente(pacienteMapper.mapFrom(pacienteDto));
            return ResponseEntity.ok("Paciente actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar datos: " + e.getMessage());
        }
    }
}

