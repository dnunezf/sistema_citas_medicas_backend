package com.example.sistema_citas_medicas_backend.mappers.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements Mapper<MedicoEntity, MedicoDto> {
    private ModelMapper modelMapper;

    public MedicoMapper(ModelMapper modelMapper)
    {
        this.modelMapper = modelMapper;

        this.modelMapper.typeMap(MedicoEntity.class, MedicoDto.class)
                .addMappings(mapper -> mapper.map(MedicoEntity::getRutaFotoPerfil, MedicoDto::setRutaFotoPerfil));
    }

    @Override
    public MedicoDto mapTo(MedicoEntity medicoEntity) {
        return modelMapper.map(medicoEntity, MedicoDto.class);
    }

    @Override
    public MedicoEntity mapFrom(MedicoDto medicoDto) {
        return modelMapper.map(medicoDto, MedicoEntity.class);
    }
}
