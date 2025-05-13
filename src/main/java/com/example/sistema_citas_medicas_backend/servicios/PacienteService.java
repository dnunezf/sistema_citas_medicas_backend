package com.example.sistema_citas_medicas_backend.servicios;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface PacienteService {
    PacienteEntity obtenerPorId(Long id);

    void actualizarPaciente(PacienteEntity paciente);

    Optional<PacienteEntity> findOne(Long id);
}