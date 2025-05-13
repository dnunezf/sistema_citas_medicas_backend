package com.example.sistema_citas_medicas_backend.dto;

public class UsuarioDto
{
    private Long id;
    private String nombre;
    private String clave;
    private String rol;

    public UsuarioDto() {}

    public UsuarioDto(Long id, String nombre, String clave, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
    }

    public UsuarioDto(Long id, String nombre, String rol) {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "UsuarioDto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", clave='" + clave + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
