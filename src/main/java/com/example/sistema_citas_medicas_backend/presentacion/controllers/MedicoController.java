package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.mappers.impl.MedicoMapper;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*") // ✅ para desarrollo con React (puedes ajustar esto)
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;

    public MedicoController(MedicoService medicoService, MedicoMapper medicoMapper) {
        this.medicoService = medicoService;
        this.medicoMapper = medicoMapper;
    }

    // ✅ Obtener perfil de un médico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicoDto> obtenerPerfil(@PathVariable Long id) {
        return medicoService.obtenerPorId(id)
                .map(medicoMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Actualizar perfil del médico
    @PutMapping("/{id}")
    public ResponseEntity<MedicoDto> actualizarPerfil(@PathVariable Long id, @RequestBody MedicoDto dto) {
        MedicoEntity medico = medicoMapper.mapFrom(dto);
        medico.setId(id); // aseguramos que el ID se respete
        MedicoEntity actualizado = medicoService.actualizarMedico(medico);

        return (actualizado != null)
                ? ResponseEntity.ok(medicoMapper.mapTo(actualizado))
                : ResponseEntity.notFound().build();
    }

    // ✅ Listar todos los médicos aprobados (para búsqueda)
    @GetMapping
    public ResponseEntity<List<MedicoDto>> obtenerTodos() {
        return ResponseEntity.ok(medicoService.obtenerMedicos());
    }

    // ✅ Buscar médicos por especialidad y localidad (parámetros opcionales)
    @GetMapping("/buscar")
    public ResponseEntity<List<MedicoDto>> buscarPorEspecialidadYLocalidad(
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String localidad) {
        return ResponseEntity.ok(medicoService.buscarPorEspecialidadYUbicacion(especialidad, localidad));
    }

    // ✅ Obtener lista de especialidades distintas
    @GetMapping("/especialidades")
    public ResponseEntity<List<String>> obtenerEspecialidades() {
        return ResponseEntity.ok(medicoService.obtenerEspecialidades());
    }

    // ✅ Actualizar estado de aprobación (para el admin)
    @PatchMapping("/{id}/aprobacion")
    public ResponseEntity<Void> actualizarEstadoAprobacion(
            @PathVariable Long id,
            @RequestParam MedicoEntity.EstadoAprobacion estado) {
        medicoService.actualizarEstadoAprobacion(id, estado);
        return ResponseEntity.ok().build();
    }
}
