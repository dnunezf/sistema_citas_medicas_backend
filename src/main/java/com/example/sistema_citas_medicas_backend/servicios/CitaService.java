package com.example.sistema_citas_medicas_backend.servicios;



import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {

    List<CitaDto> obtenerCitasPorMedico(Long idMedico);

    List<CitaDto> filtrarCitasPorEstado(Long idMedico, CitaEntity.EstadoCita estado);

    List<CitaDto> filtrarCitasPorPaciente(Long idMedico, String nombrePaciente);

    CitaDto actualizarCita(Long idCita, CitaEntity.EstadoCita nuevoEstado, String notas);

    Long obtenerIdMedicoPorCita(Long idCita);

    CitaDto agendarCita(Long idPaciente, Long idMedico, LocalDateTime fechaHora);


    List<LocalDateTime> obtenerEspaciosDisponibles(Long idMedico, List<HorarioMedicoDto> horarios);

    void guardarCita(CitaEntity cita);

    List<CitaDto> obtenerCitasPorPaciente(Long idPaciente);

    List<CitaDto> filtrarCitasPorEstadoPaciente(Long idPaciente, CitaEntity.EstadoCita estado);

    List<CitaDto> filtrarCitasPorNombreMedico(Long idPaciente, String nombreMedico);

    CitaDto obtenerCitaPorId(Long idCita);

    List<CitaDto> filtrarCitasPorEstadoYNombre(Long idMedico, CitaEntity.EstadoCita estado, String nombrePaciente);

    String normalizar(String texto);

    List<LocalDateTime> generarTodosLosEspacios(Long idMedico, List<HorarioMedicoDto> horarios);

    List<LocalDateTime> generarEspaciosDesdeFecha(Long idMedico, List<HorarioMedicoDto> horarios, LocalDate fechaInicio, int dias);

    List<CitaDto> filtrarCitasPorEstadoYNombreMedico(Long idPaciente, String estado, String nombreMedico);

    List<LocalDateTime> generarTodosLosEspaciosExtendido(Long idMedico, List<HorarioMedicoDto> horarios);

}