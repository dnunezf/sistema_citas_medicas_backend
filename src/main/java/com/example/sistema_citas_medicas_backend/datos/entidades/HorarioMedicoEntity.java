package com.example.sistema_citas_medicas_backend.datos.entidades;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "horarioMedico")
public class HorarioMedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_horario")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private MedicoEntity medico;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "tiempo_cita", nullable = false)
    private int tiempoCita; // Duración de la cita en minutos

    public enum DiaSemana {
        lunes, martes, miercoles, jueves, viernes, sabado, domingo
    }

    // Constructor vacío
    public HorarioMedicoEntity() {}

    // Constructor con parámetros
    public HorarioMedicoEntity(MedicoEntity medico, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin, int tiempoCita) {
        this.medico = medico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tiempoCita = tiempoCita;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicoEntity getMedico() { return medico; }
    public void setMedico(MedicoEntity medico) { this.medico = medico; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public int getTiempoCita() { return tiempoCita; }
    public void setTiempoCita(int tiempoCita) { this.tiempoCita = tiempoCita; }
}
