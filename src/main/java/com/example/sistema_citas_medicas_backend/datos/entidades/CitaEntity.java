package com.example.sistema_citas_medicas_backend.datos.entidades;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_cita")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "medico", nullable = false)
    private MedicoEntity medico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCita estado;

    @Column(name = "notas")
    private String notas;

    public enum EstadoCita {
        pendiente, confirmada, cancelada, completada
    }

    public CitaEntity() {}

    public CitaEntity(PacienteEntity paciente, MedicoEntity medico, LocalDateTime fechaHora, EstadoCita estado, String notas) {
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.notas = notas;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }

    public MedicoEntity getMedico() { return medico; }
    public void setMedico(MedicoEntity medico) { this.medico = medico; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}