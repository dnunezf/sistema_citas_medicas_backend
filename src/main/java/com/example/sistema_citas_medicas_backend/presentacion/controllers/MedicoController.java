package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {

    private final MedicoService medicoService;
    private final Mapper<MedicoEntity, MedicoDto> medicoMapper;

    public MedicoController(MedicoService medicoService, Mapper<MedicoEntity, MedicoDto> medicoMapper) {
        this.medicoService = medicoService;
        this.medicoMapper = medicoMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'PACIENTE')")
    public ResponseEntity<MedicoDto> obtenerMedico(@PathVariable Long id) {
        return medicoService.obtenerPorId(id)
                .map(medicoMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<?> actualizarMedicoConFoto(
            @PathVariable Long id,
            @ModelAttribute MedicoDto dto,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil
    ) {
        UsuarioPrincipal principal = (UsuarioPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validar que solo pueda modificar su propio perfil
        if (!principal.getUsuarioEntity().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para editar este perfil.");
        }

        Optional<MedicoEntity> medicoOpt = medicoService.obtenerPorId(id);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MedicoEntity existente = medicoOpt.get();

        // ✅ Validar que el médico esté aprobado
        if (existente.getEstadoAprobacion() != MedicoEntity.EstadoAprobacion.aprobado) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Su cuenta aún no ha sido aprobada. No puede modificar su perfil.");
        }

        // Actualizar datos del perfil
        existente.setNombre(dto.getNombre());
        existente.setEspecialidad(dto.getEspecialidad());
        existente.setCostoConsulta(dto.getCostoConsulta());
        existente.setLocalidad(dto.getLocalidad());
        existente.setFrecuenciaCitas(dto.getFrecuenciaCitas());
        existente.setPresentacion(dto.getPresentacion());

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            try {
                String nombreArchivo = "medico_" + id + "_" + System.currentTimeMillis() + "_" + fotoPerfil.getOriginalFilename();
                Path rutaCarpeta = Paths.get("uploads/fotos_perfil");
                Files.createDirectories(rutaCarpeta);

                Path rutaCompleta = rutaCarpeta.resolve(nombreArchivo);
                Files.copy(fotoPerfil.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                String rutaRelativa = "/uploads/fotos_perfil/" + nombreArchivo;
                existente.setRutaFotoPerfil(rutaRelativa);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Error al subir la foto.");
            }
        }

        MedicoEntity actualizado = medicoService.actualizarMedico(existente);
        return ResponseEntity.ok(medicoMapper.mapTo(actualizado));
    }
}
