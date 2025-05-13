// ESTE DTO ES PARA EL REGISTRO DE UN MEDICO INICIAL, SIN FOTO NI ESTADO DE APROBACION

package com.example.sistema_citas_medicas_backend.dto;

public class MedicoRegistroDto {
    private Long id;
    private String nombre;
    private String clave;
    private String especialidad;
    private Double costoConsulta;
    private String localidad;
    private int frecuenciaCitas;
    private String presentacion;

    public MedicoRegistroDto() {
    }

    public MedicoRegistroDto(Long id, String nombre, String clave, String especialidad, Double costoConsulta, String localidad, int frecuenciaCitas, String presentacion) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.especialidad = especialidad;
        this.costoConsulta = costoConsulta;
        this.localidad = localidad;
        this.frecuenciaCitas = frecuenciaCitas;
        this.presentacion = presentacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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
}