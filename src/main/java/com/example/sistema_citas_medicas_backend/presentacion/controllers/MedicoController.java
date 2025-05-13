package com.example.sistema_citas_medicas_backend.presentacion.controllers;

import com.example.sistema_citas_medicas_backend.mappers.impl.MedicoMapper;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
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
}
