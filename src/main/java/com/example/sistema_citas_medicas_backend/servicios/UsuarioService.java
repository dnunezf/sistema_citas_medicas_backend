package com.example.sistema_citas_medicas_backend.servicios;

import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioEntity save(UsuarioEntity usuario);

    List<UsuarioEntity> findAll();

    Optional<UsuarioEntity> findOne(Long id);

    boolean isExists(Long id);

    UsuarioEntity partialUpdate(Long id, UsuarioEntity usuario);

    void delete(Long id);

    Optional<UsuarioEntity> login(Long id, String clave);

    Optional<UsuarioEntity> findById(Long id);

}

