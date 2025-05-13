package com.example.sistema_citas_medicas_backend.dto;

public class MedicoDto extends UsuarioDto {

    private Long id;
    private String especialidad;
    private Double costoConsulta;
    private String localidad;
    private int frecuenciaCitas;
    private String presentacion;
    private String estadoAprobacion;
    private String rutaFotoPerfil;

    public MedicoDto() {}

    public MedicoDto(Long id, String nombre, String clave, String especialidad, Double costoConsulta,
                     String localidad, int frecuenciaCitas, String presentacion, String estadoAprobacion) {
        super(id, nombre, clave, "MEDICO");
        this.id = id;
        this.especialidad = especialidad;
        this.costoConsulta = costoConsulta;
        this.localidad = localidad;
        this.frecuenciaCitas = frecuenciaCitas;
        this.presentacion = presentacion;
        this.estadoAprobacion = estadoAprobacion;
        this.rutaFotoPerfil = "";
    }

    public MedicoDto(Long id, String nombre, String clave, String especialidad, Double costoConsulta,
                     String localidad, int frecuenciaCitas, String presentacion, String estadoAprobacion, String rutaFotoPerfil) {
        super(id, nombre, clave, "MEDICO");
        this.id = id;
        this.especialidad = especialidad;
        this.costoConsulta = costoConsulta;
        this.localidad = localidad;
        this.frecuenciaCitas = frecuenciaCitas;
        this.presentacion = presentacion;
        this.estadoAprobacion = estadoAprobacion;
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Double getCostoConsulta() {
        return costoConsulta;
    }

    public void setCostoConsulta(Double costoConsulta) {
        this.costoConsulta = costoConsulta;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public int getFrecuenciaCitas() {
        return frecuenciaCitas;
    }

    public void setFrecuenciaCitas(int frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getEstadoAprobacion() {
        return estadoAprobacion;
    }

    public void setEstadoAprobacion(String estadoAprobacion) {
        this.estadoAprobacion = estadoAprobacion;
    }

    public String getRutaFotoPerfil() {
        return rutaFotoPerfil;
    }

    public void setRutaFotoPerfil(String rutaFotoPerfil) {
        this.rutaFotoPerfil = rutaFotoPerfil;
    }
}