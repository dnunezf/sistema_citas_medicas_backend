package com.example.sistema_citas_medicas_backend.mappers.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements Mapper<UsuarioEntity, UsuarioDto> {
    private final ModelMapper modelMapper;

    public UsuarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UsuarioDto mapTo(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDto.class);
    }

    @Override
    public UsuarioEntity mapFrom(UsuarioDto usuarioDto) {
        return modelMapper.map(usuarioDto, UsuarioEntity.class);
    }
}
