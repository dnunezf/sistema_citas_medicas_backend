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

    public UsuarioPrincipal(UsuarioDto usuario, UsuarioEntity usuarioEntity) {
        this.usuario = usuario;
        this.usuarioEntity = usuarioEntity;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public UsuarioEntity getUsuarioEntity() {
        return usuarioEntity;
    }

    public void setUsuarioEntity(UsuarioEntity usuarioEntity) {
        this.usuarioEntity = usuarioEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aquí se asegura que Spring interprete correctamente el rol
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    @Override
    public String getPassword() {
        return usuario.getClave();
    }

    @Override
    public String getUsername() {
        return String.valueOf(usuario.getId());  // ✅ Usa el ID como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
