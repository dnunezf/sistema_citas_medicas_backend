package com.example.sistema_citas_medicas_backend.servicios.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.UsuarioRepository;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              MedicoRepository medicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    @Transactional
    public UsuarioEntity save(UsuarioEntity usuario) {

        if (usuario instanceof MedicoEntity) {
            return medicoRepository.save((MedicoEntity) usuario);
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioEntity> findAll() {
        return StreamSupport.stream(usuarioRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioEntity> findOne(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public UsuarioEntity partialUpdate(Long id, UsuarioEntity usuario) {
        return usuarioRepository.findById(id).map(existingUser -> {
            Optional.ofNullable(usuario.getNombre()).ifPresent(existingUser::setNombre);
            Optional.ofNullable(usuario.getClave()).ifPresent(existingUser::setClave); // sin encriptar
            Optional.ofNullable(usuario.getRol()).ifPresent(existingUser::setRol);
            return usuarioRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado!"));
    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<UsuarioEntity> login(Long id, String claveNoEncriptada) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            // Comparación directa (insegura, solo para pruebas)
            if (claveNoEncriptada.equals(usuario.getClave())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<UsuarioEntity> findById(Long id) {
        return usuarioRepository.findById(id);
    }

}
