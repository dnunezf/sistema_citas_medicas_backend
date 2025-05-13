package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoRegistroDto;
import com.example.sistema_citas_medicas_backend.mappers.impl.MedicoMapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final UsuarioService usuarioService;
    private final MedicoMapper medicoMapper;

    public MedicoController(UsuarioService usuarioService, MedicoMapper medicoMapper) {
        this.usuarioService = usuarioService;
        this.medicoMapper = medicoMapper;
    }

    // ESTE POST LO TRABAJAMOS CON EL MedicoRegistroDto, PORQUE DE MOMENTO AL REGISTRAR UN MEDICO ESTA PENDIENTE DE
    // APROBAR Y NO SE ACTUALIZA SU FOTO DE PERFIL
    @PostMapping("/registro")
    public ResponseEntity<?> registrarMedico(@Valid @RequestBody MedicoRegistroDto dto) {
        if (usuarioService.isExists(dto.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese ID.");
        }

        // Crear entidad médica
        MedicoEntity medico = new MedicoEntity(
                dto.getId(),
                dto.getNombre(),
                dto.getClave(),
                dto.getEspecialidad(),
                dto.getCostoConsulta(),
                dto.getLocalidad(),
                dto.getFrecuenciaCitas(),
                dto.getPresentacion(),
                MedicoEntity.EstadoAprobacion.pendiente
        );

        usuarioService.save(medico);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registro enviado. Esperando aprobación del administrador.");
    }
}
