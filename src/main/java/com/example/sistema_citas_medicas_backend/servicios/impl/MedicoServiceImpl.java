package com.example.sistema_citas_medicas_backend.servicios.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.mappers.impl.MedicoMapper;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicoServiceImpl implements MedicoService {
    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;

    public MedicoServiceImpl(MedicoRepository medicoRepository, MedicoMapper medicoMapper) {
        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
    }

    public Optional<MedicoEntity> obtenerPorId(Long id) {
        return medicoRepository.findById(id);
    }


    @Transactional
    public MedicoEntity actualizarMedico(MedicoEntity medico) {
        return medicoRepository.findById(medico.getId()).map(medicoExistente -> {
            medicoExistente.setNombre(medico.getNombre());
            medicoExistente.setEspecialidad(medico.getEspecialidad());
            medicoExistente.setCostoConsulta(medico.getCostoConsulta());
            medicoExistente.setLocalidad(medico.getLocalidad());
            medicoExistente.setFrecuenciaCitas(medico.getFrecuenciaCitas());
            medicoExistente.setPresentacion(medico.getPresentacion());
            medicoExistente.setEstadoAprobacion(medico.getEstadoAprobacion());
            medicoExistente.setRutaFotoPerfil(medico.getRutaFotoPerfil());
            return medicoRepository.save(medicoExistente);
        }).orElse(null);
    }

    public List<MedicoEntity> obtenerTodosMedicos() {
        return medicoRepository.findAll();
    }


    public List<MedicoDto> obtenerMedicos() {
        List<MedicoEntity> medicos = medicoRepository.findAll();
        return medicos.stream()
                .map(medicoMapper::mapTo)
                .collect(Collectors.toList());
    }


    @Transactional
    public void actualizarEstadoAprobacion(Long id, MedicoEntity.EstadoAprobacion estado) {
        MedicoEntity medico = medicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        medico.setEstadoAprobacion(estado);
        medicoRepository.save(medico);
    }


    @Override
    public List<MedicoDto> buscarPorEspecialidadYUbicacion(String especialidad, String ubicacion) {
        String esp = (especialidad != null && !especialidad.trim().isEmpty()) ? especialidad : null;
        String loc = (ubicacion != null && !ubicacion.trim().isEmpty()) ? ubicacion : null;

        List<MedicoEntity> medicos = medicoRepository.buscarPorEspecialidadYLocalidad(esp, loc);

        return medicos.stream()
                .map(medicoMapper::mapTo)
                .collect(Collectors.toList());
    }


    @Override
    public List<String> obtenerEspecialidades() {
        return medicoRepository.findDistinctEspecialidades();
    }


}