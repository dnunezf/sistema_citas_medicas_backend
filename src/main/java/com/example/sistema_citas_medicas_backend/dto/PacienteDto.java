package com.example.sistema_citas_medicas_backend.dto;

import java.time.LocalDate;

public class PacienteDto extends UsuarioDto {
    private LocalDate fechaNacimiento;
    private String telefono;
    private String direccion;

    public PacienteDto() {}

    public PacienteDto(Long id, String nombre, String clave, LocalDate fechaNacimiento, String telefono, String direccion) {
        super(id, nombre, clave, "PACIENTE");
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y Setters
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}