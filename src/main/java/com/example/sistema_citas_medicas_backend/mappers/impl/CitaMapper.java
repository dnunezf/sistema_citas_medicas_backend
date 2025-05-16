package com.example.sistema_citas_medicas_backend.mappers.impl;


import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements Mapper<CitaEntity, CitaDto> {
    private final ModelMapper modelMapper;

    public CitaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CitaDto mapTo(CitaEntity citaEntity) {
        CitaDto dto = modelMapper.map(citaEntity, CitaDto.class);
        dto.setIdPaciente(citaEntity.getPaciente().getId());
        dto.setNombrePaciente(citaEntity.getPaciente().getNombre());
        dto.setIdMedico(citaEntity.getMedico().getId());
        dto.setNombreMedico(citaEntity.getMedico().getNombre());
        return dto;
    }

    @Override
    public CitaEntity mapFrom(CitaDto citaDto) {
        return modelMapper.map(citaDto, CitaEntity.class);
    }
}