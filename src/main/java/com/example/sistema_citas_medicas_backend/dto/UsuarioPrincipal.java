package com.example.sistema_citas_medicas_backend.dto;

import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final UsuarioDto usuario;
    private UsuarioEntity usuarioEntity;

    // Constructor que recibe tanto DTO como Entity
    public UsuarioPrincipal(UsuarioDto usuario, UsuarioEntity usuarioEntity) {
        this.usuario = usuario;
        this.usuarioEntity = usuarioEntity;
    }

    // Getter para el DTO
    public UsuarioDto getUsuario() {
        return usuario;
    }

    // Getter para la entidad, con setter si necesitas modificar después
    public UsuarioEntity getUsuarioEntity() {
        return usuarioEntity;
    }

    public void setUsuarioEntity(UsuarioEntity usuarioEntity) {
        this.usuarioEntity = usuarioEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }


    @Override
    public String getPassword() {
        return usuario.getClave();
    }

    @Override
    public String getUsername() {
        return usuario.getNombre();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Personaliza según necesidad
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Personaliza según necesidad
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Personaliza según necesidad
    }

    @Override
    public boolean isEnabled() {
        return true; // Personaliza según necesidad
    }
}
