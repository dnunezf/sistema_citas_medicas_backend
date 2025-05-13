package com.example.sistema_citas_medicas_backend.servicios;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;

import java.util.List;
import java.util.Optional;

public interface MedicoService {

    Optional<MedicoEntity> obtenerPorId(Long id);

    MedicoEntity  actualizarMedico(MedicoEntity medico);

    List<MedicoEntity> obtenerTodosMedicos();

    List<MedicoDto> obtenerMedicos();

    void actualizarEstadoAprobacion(Long id, MedicoEntity.EstadoAprobacion estado);

    List<MedicoDto> buscarPorEspecialidadYUbicacion(String especialidad, String ubicacion);

    List<String> obtenerEspecialidades();

}