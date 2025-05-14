package com.example.sistema_citas_medicas_backend.mappers.impl;


import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.dto.PacienteDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements Mapper<PacienteEntity, PacienteDto> {
    private final ModelMapper modelMapper;

    public PacienteMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PacienteDto mapTo(PacienteEntity pacienteEntity) {
        return modelMapper.map(pacienteEntity, PacienteDto.class);
    }

    @Override
    public PacienteEntity mapFrom(PacienteDto pacienteDto) {
        return modelMapper.map(pacienteDto, PacienteEntity.class);
    }
}