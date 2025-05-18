package com.example.sistema_citas_medicas_backend.servicios.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.servicios.PacienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public PacienteEntity obtenerPorId(Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void actualizarPaciente(PacienteEntity paciente) {
        PacienteEntity pacienteExistente = pacienteRepository.findById(paciente.getId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        pacienteExistente.setNombre(paciente.getNombre());
        pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        pacienteExistente.setTelefono(paciente.getTelefono());
        pacienteExistente.setDireccion(paciente.getDireccion());

        // Solo actualiza la clave si viene una nueva (y no está vacía)
        if (paciente.getClave() != null && !paciente.getClave().isBlank()) {
            pacienteExistente.setClave(paciente.getClave());
        }

        pacienteRepository.save(pacienteExistente);
    }


    @Override
    public Optional<PacienteEntity> findOne(Long id) {
        return pacienteRepository.findById(id);
    }
}
