package com.example.sistema_citas_medicas_backend.servicios.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.UsuarioRepository;
import com.example.sistema_citas_medicas_backend.servicios.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;  // Inyectar

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              MedicoRepository medicoRepository,
                              PacienteRepository pacienteRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioEntity save(UsuarioEntity usuario) {
        // Cifrar contraseña antes de guardar
        if (usuario.getClave() != null && !usuario.getClave().startsWith("$2a$")) {
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        }

        if (usuario instanceof MedicoEntity) {
            return medicoRepository.save((MedicoEntity) usuario);
        } else if (usuario instanceof PacienteEntity) {
            return pacienteRepository.save((PacienteEntity) usuario);
        } else {
            return usuarioRepository.save(usuario);
        }
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
            Optional.ofNullable(usuario.getClave()).ifPresent(clave -> {
                // Cifrar si no está cifrada
                if (!clave.startsWith("$2a$")) {
                    existingUser.setClave(passwordEncoder.encode(clave));
                } else {
                    existingUser.setClave(clave);
                }
            });
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
            // Comparar contraseña usando passwordEncoder
            if (passwordEncoder.matches(claveNoEncriptada, usuario.getClave())) {
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
