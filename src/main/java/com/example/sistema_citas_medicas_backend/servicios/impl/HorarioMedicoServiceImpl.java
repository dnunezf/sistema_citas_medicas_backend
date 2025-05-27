package com.example.sistema_citas_medicas_backend.servicios.impl;


import com.example.sistema_citas_medicas_backend.datos.entidades.HorarioMedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.HorarioMedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioMedicoServiceImpl implements HorarioMedicoService {

    private final HorarioMedicoRepository horarioMedicoRepository;
    private final MedicoRepository medicoRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


    @Autowired
    private final ModelMapper modelMapper;

    public HorarioMedicoServiceImpl(HorarioMedicoRepository horarioMedicoRepository, MedicoRepository medicoRepository, ModelMapper modelMapper) {
        this.horarioMedicoRepository = horarioMedicoRepository;
        this.medicoRepository = medicoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<HorarioMedicoDto> obtenerHorariosPorMedico(Long idMedico) {
        List<HorarioMedicoEntity> horarios = horarioMedicoRepository.findByMedicoId(idMedico);

        if (horarios == null || horarios.isEmpty()) {
            return new ArrayList<>();
        }

        return horarios.stream()
                .map(horario -> modelMapper.map(horario, HorarioMedicoDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public HorarioMedicoDto obtenerHorarioPorId(Long idHorario) {
        HorarioMedicoEntity horario = horarioMedicoRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        return modelMapper.map(horario, HorarioMedicoDto.class);
    }

    @Override
    public HorarioMedicoEntity guardarHorario(HorarioMedicoDto horarioDto) {
        HorarioMedicoEntity horario = new HorarioMedicoEntity();

        horario.setDiaSemana(HorarioMedicoEntity.DiaSemana.valueOf(horarioDto.getDiaSemana().toLowerCase()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        horario.setHoraInicio(LocalTime.parse(horarioDto.getHoraInicio(), formatter));
        horario.setHoraFin(LocalTime.parse(horarioDto.getHoraFin(), formatter));
        horario.setTiempoCita(horarioDto.getTiempoCita());

        MedicoEntity medico = medicoRepository.findById(horarioDto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        horario.setMedico(medico);

        return horarioMedicoRepository.save(horario);
    }



    @Override
    public HorarioMedicoDto actualizarHorario(Long idHorario, HorarioMedicoDto horarioDto) {
        return horarioMedicoRepository.findById(idHorario).map(horarioExistente -> {
            try {
                HorarioMedicoEntity.DiaSemana diaSemanaEnum = HorarioMedicoEntity.DiaSemana.valueOf(horarioDto.getDiaSemana().toLowerCase());
                horarioExistente.setDiaSemana(diaSemanaEnum);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Día de la semana inválido: " + horarioDto.getDiaSemana());
            }

            horarioExistente.setHoraInicio(LocalTime.parse(horarioDto.getHoraInicio()));
            horarioExistente.setHoraFin(LocalTime.parse(horarioDto.getHoraFin()));
            horarioExistente.setTiempoCita(horarioDto.getTiempoCita());
            HorarioMedicoEntity actualizado = horarioMedicoRepository.save(horarioExistente);
            return modelMapper.map(actualizado, HorarioMedicoDto.class);
        }).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }

    @Override
    public void eliminarHorario(Long idHorario) {
        if (!horarioMedicoRepository.existsById(idHorario)) {
            throw new RuntimeException("Horario no encontrado");
        }
        horarioMedicoRepository.deleteById(idHorario);
    }

    @Override
    public Long obtenerIdMedicoPorHorario(Long idHorario) {
        return horarioMedicoRepository.findIdMedicoByHorario(idHorario);
    }

}

