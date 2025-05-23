package com.example.sistema_citas_medicas_backend.servicios;


import com.example.sistema_citas_medicas_backend.datos.entidades.HorarioMedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;

import java.util.List;

public interface HorarioMedicoService {

    List<HorarioMedicoDto> obtenerHorariosPorMedico(Long idMedico);

    HorarioMedicoDto obtenerHorarioPorId(Long idHorario);

    HorarioMedicoEntity guardarHorario(HorarioMedicoDto horarioDto);

    HorarioMedicoDto actualizarHorario(Long idHorario, HorarioMedicoDto horarioDto);

    void eliminarHorario(Long idHorario);

    Long obtenerIdMedicoPorHorario(Long idHorario);


}

