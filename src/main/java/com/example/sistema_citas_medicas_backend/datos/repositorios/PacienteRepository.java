package com.example.sistema_citas_medicas_backend.datos.repositorios;

import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {
    List<PacienteEntity> findAll();
}