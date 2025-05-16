package com.example.sistema_citas_medicas_backend.datos.repositorios;


import com.example.sistema_citas_medicas_backend.datos.entidades.HorarioMedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedicoEntity, Long> {

    @Query("SELECT h FROM HorarioMedicoEntity h WHERE h.medico.id = :idMedico")
    List<HorarioMedicoEntity> findByMedicoId(@Param("idMedico") Long idMedico);

    @Query("SELECT h.medico.id FROM HorarioMedicoEntity h WHERE h.id = :idHorario")
    Long findIdMedicoByHorario(@Param("idHorario") Long idHorario);

}

