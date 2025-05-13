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

        // ⚠️ Sin codificar contraseñas por ahora
        if (!paciente.getClave().equals(pacienteExistente.getClave())) {
            pacienteExistente.setClave(paciente.getClave());
        }

        pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        pacienteExistente.setTelefono(paciente.getTelefono());
        pacienteExistente.setDireccion(paciente.getDireccion());

        pacienteRepository.save(pacienteExistente);
    }

    @Override
    public Optional<PacienteEntity> findOne(Long id) {
        return pacienteRepository.findById(id);
    }
}
