package com.example.sistema_citas_medicas_backend.config;

import com.example.sistema_citas_medicas_backend.datos.entidades.RolUsuario;
import com.example.sistema_citas_medicas_backend.datos.entidades.UsuarioEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Long adminId = 999999999L;
            if (!usuarioRepository.existsById(adminId)) {
                UsuarioEntity admin = new UsuarioEntity();
                admin.setId(adminId);
                admin.setNombre("Administrador General");
                admin.setRol(RolUsuario.ADMINISTRADOR);
                admin.setClave(passwordEncoder.encode("admin123"));

                usuarioRepository.save(admin);
                System.out.println("Usuario administrador creado por defecto.");
            } else {
                System.out.println("Usuario administrador ya existe.");
            }
        };
    }
}
