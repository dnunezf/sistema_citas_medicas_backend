package com.example.sistema_citas_medicas_backend.datos.repositorios;


import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<CitaEntity, Long> {

    @Query("SELECT c FROM CitaEntity c WHERE c.medico.id = :idMedico ORDER BY c.fechaHora DESC")
    List<CitaEntity> findByMedicoOrdenadas(@Param("idMedico") Long idMedico);

    @Query("SELECT c FROM CitaEntity c WHERE c.medico.id = :idMedico AND c.estado = :estado ORDER BY c.fechaHora DESC")
    List<CitaEntity> findByMedicoAndEstado(@Param("idMedico") Long idMedico, @Param("estado") CitaEntity.EstadoCita estado);

    @Query("SELECT c FROM CitaEntity c WHERE c.medico.id = :idMedico AND c.paciente.nombre LIKE %:nombrePaciente% ORDER BY c.fechaHora DESC")
    List<CitaEntity> findByMedicoAndPaciente(@Param("idMedico") Long idMedico, @Param("nombrePaciente") String nombrePaciente);


    boolean existsByMedicoAndFechaHora(MedicoEntity medico, LocalDateTime fechaHora);

    List<CitaEntity> findByPacienteId(Long idPaciente);

    @Query("SELECT c FROM CitaEntity c WHERE c.medico.id = :idMedico AND LOWER(c.paciente.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.fechaHora DESC")
    List<CitaEntity> buscarPorNombrePaciente(@Param("idMedico") Long idMedico, @Param("nombre") String nombre);

    @Query("SELECT c FROM CitaEntity c WHERE c.medico = :medico AND c.fechaHora = :fechaHora")
    CitaEntity findByMedicoAndFechaHora(@Param("medico") MedicoEntity medico, @Param("fechaHora") LocalDateTime fechaHora);

    @Query("SELECT c FROM CitaEntity c WHERE c.medico.id = :idMedico AND c.fechaHora BETWEEN :inicio AND :fin")
    List<CitaEntity> findByMedicoIdAndFechaHoraBetween(@Param("idMedico") Long idMedico,
                                                       @Param("inicio") LocalDateTime inicio,
                                                       @Param("fin") LocalDateTime fin);


    boolean existsByPacienteAndFechaHora(PacienteEntity paciente, LocalDateTime fechaHora);

}