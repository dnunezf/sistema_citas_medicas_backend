package com.example.sistema_citas_medicas_backend.Security;

import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.UsuarioRepository;
import com.example.sistema_citas_medicas_backend.dto.UsuarioDto;
import com.example.sistema_citas_medicas_backend.dto.UsuarioPrincipal;
import com.example.sistema_citas_medicas_backend.mappers.impl.UsuarioMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String idStr) throws UsernameNotFoundException {
        Long id;
        try {
            id = Long.parseLong(idStr);  // Convertir string a Long
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("ID inválido");
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        UsuarioDto usuarioDto = usuarioMapper.mapTo(usuarioEntity);
        return new UsuarioPrincipal(usuarioDto, usuarioEntity);
    }




}


