package com.example.sistema_citas_medicas_backend.dto;

public class HorarioMedicoDto {
    private Long id;
    private Long idMedico;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private int tiempoCita;

    public HorarioMedicoDto() {}

    public HorarioMedicoDto(Long id, Long idMedico, String diaSemana, String horaInicio, String horaFin, int tiempoCita) {
        this.id = id;
        this.idMedico = idMedico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tiempoCita = tiempoCita;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdMedico() { return idMedico; }
    public void setIdMedico(Long idMedico) { this.idMedico = idMedico; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
   public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public int getTiempoCita() { return tiempoCita; }
    public void setTiempoCita(int tiempoCita) { this.tiempoCita = tiempoCita; }
}
