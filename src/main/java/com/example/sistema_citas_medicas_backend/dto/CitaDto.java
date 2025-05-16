package com.example.sistema_citas_medicas_backend.dto;

import java.time.LocalDateTime;

public class CitaDto {
    private Long id;
    private Long idPaciente;
    private String nombrePaciente;
    private Long idMedico;
    private String nombreMedico;
    private LocalDateTime fechaHora;
    private String estado;
    private String notas;

    public CitaDto() {}

    public CitaDto(Long id, Long idPaciente, String nombrePaciente, Long idMedico, String nombreMedico, LocalDateTime fechaHora, String estado, String notas) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.nombrePaciente = nombrePaciente;
        this.idMedico = idMedico;
        this.nombreMedico = nombreMedico;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.notas = notas;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public Long getIdMedico() { return idMedico; }
    public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}