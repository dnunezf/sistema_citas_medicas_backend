package com.example.sistema_citas_medicas_backend.servicios.impl;

import com.example.sistema_citas_medicas_backend.datos.entidades.CitaEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.MedicoEntity;
import com.example.sistema_citas_medicas_backend.datos.entidades.PacienteEntity;
import com.example.sistema_citas_medicas_backend.datos.repositorios.CitaRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.HorarioMedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.MedicoRepository;
import com.example.sistema_citas_medicas_backend.datos.repositorios.PacienteRepository;
import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ModelMapper modelMapper;
;

    public CitaServiceImpl(CitaRepository citaRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository , HorarioMedicoRepository horarioMedicoRepository, PacienteRepository pacienteRepository1, ModelMapper modelMapper ) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository1;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CitaDto> obtenerCitasPorMedico(Long idMedico) {
        List<CitaEntity> citas = citaRepository.findByMedicoOrdenadas(idMedico);

        return citas.stream().map(cita -> {
            CitaDto citaDto = modelMapper.map(cita, CitaDto.class);

            if (cita.getPaciente() != null) {
                citaDto.setIdPaciente(cita.getPaciente().getId());
                citaDto.setNombrePaciente(cita.getPaciente().getNombre());
            }

            if (cita.getMedico() != null) {
                citaDto.setIdMedico(cita.getMedico().getId());
                citaDto.setNombreMedico(cita.getMedico().getNombre());
            }

            return citaDto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<CitaDto> filtrarCitasPorEstado(Long idMedico, CitaEntity.EstadoCita estado) {
        List<CitaEntity> citas = citaRepository.findByMedicoAndEstado(idMedico, estado);
        return citas.stream().map(cita -> modelMapper.map(cita, CitaDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<CitaDto> filtrarCitasPorPaciente(Long idMedico, String nombrePaciente) {
        List<CitaEntity> citas = citaRepository.buscarPorNombrePaciente(idMedico, nombrePaciente);
        return citas.stream().map(cita -> {
            CitaDto dto = modelMapper.map(cita, CitaDto.class);
            if (cita.getPaciente() != null) {
                dto.setIdPaciente(cita.getPaciente().getId());
                dto.setNombrePaciente(cita.getPaciente().getNombre());
            }
            if (cita.getMedico() != null) {
                dto.setIdMedico(cita.getMedico().getId());
                dto.setNombreMedico(cita.getMedico().getNombre());
            }
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public CitaDto actualizarCita(Long idCita, CitaEntity.EstadoCita estado, String notas) {
        CitaEntity cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(estado);
        if (notas != null) {
            cita.setNotas(notas);
        }
        CitaEntity citaActualizada = citaRepository.save(cita);
        return modelMapper.map(citaActualizada, CitaDto.class);
    }


    @Override
    public Long obtenerIdMedicoPorCita(Long idCita) {
        return citaRepository.findById(idCita)
                .map(cita -> cita.getMedico().getId())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
    }


    @Override
    public List<LocalDateTime> obtenerEspaciosDisponibles(Long idMedico, List<HorarioMedicoDto> horarios) {
        List<LocalDateTime> espaciosDisponibles = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate fecha = fechaActual.plusDays(i);
            String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "CR"));

            for (HorarioMedicoDto horario : horarios) {
                if (normalizar(diaSemana).equals(horario.getDiaSemana().toLowerCase())) {
                    LocalTime horaInicio = LocalTime.parse(horario.getHoraInicio());
                    LocalTime horaFin = LocalTime.parse(horario.getHoraFin());
                    int duracion = horario.getTiempoCita();

                    LocalDateTime espacio = LocalDateTime.of(fecha, horaInicio);
                    while (!espacio.toLocalTime().isAfter(horaFin.minusMinutes(duracion))) {
                        boolean ocupado = citaRepository.existsByMedicoAndFechaHora(
                                medicoRepository.getReferenceById(idMedico), espacio);

                        if (!ocupado) {
                            espaciosDisponibles.add(espacio);
                        }

                        espacio = espacio.plusMinutes(duracion);
                    }
                }
            }
        }

        return espaciosDisponibles;
    }

    @Override
    @Transactional
    public CitaDto agendarCita(Long idPaciente, Long idMedico, LocalDateTime fechaHora) {
        PacienteEntity paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        MedicoEntity medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        boolean existeCitaMedico = citaRepository.existsByMedicoAndFechaHora(medico, fechaHora);
        if (existeCitaMedico) {
            throw new RuntimeException("El horario seleccionado ya está ocupado por el médico.");
        }

        boolean existeCitaPaciente = citaRepository.existsByPacienteAndFechaHora(paciente, fechaHora);
        if (existeCitaPaciente) {
            throw new RuntimeException("Ya tienes una cita agendada a esa hora.");
        }

        CitaEntity nuevaCita = new CitaEntity(paciente, medico, fechaHora, CitaEntity.EstadoCita.pendiente, "");
        nuevaCita = citaRepository.save(nuevaCita);

        return modelMapper.map(nuevaCita, CitaDto.class);
    }


    @Override
    public void guardarCita(CitaEntity cita) {
        citaRepository.save(cita);
    }


    @Override
    public List<CitaDto> obtenerCitasPorPaciente(Long idPaciente) {
        List<CitaEntity> citas = citaRepository.findByPacienteId(idPaciente);
        return citas.stream()
                .sorted((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()))
                .map(cita -> modelMapper.map(cita, CitaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDto> filtrarCitasPorEstadoPaciente(Long idPaciente, CitaEntity.EstadoCita estado) {
        List<CitaEntity> citas = citaRepository.findByPacienteId(idPaciente).stream()
                .filter(cita -> cita.getEstado().equals(estado))
                .sorted((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()))
                .collect(Collectors.toList());

        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDto> filtrarCitasPorNombreMedico(Long idPaciente, String nombreMedico) {
        List<CitaEntity> citas = citaRepository.findByPacienteId(idPaciente).stream()
                .filter(cita -> cita.getMedico().getNombre().toLowerCase().contains(nombreMedico.toLowerCase()))
                .sorted((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()))
                .collect(Collectors.toList());

        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CitaDto obtenerCitaPorId(Long idCita) {
        CitaEntity cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + idCita));

        return new CitaDto(
                cita.getId(),
                cita.getPaciente().getId(),
                cita.getPaciente().getNombre(),
                cita.getMedico().getId(),
                cita.getMedico().getNombre(),
                cita.getFechaHora(),
                cita.getEstado().name(),
                cita.getNotas()
        );
    }

    @Override
    public List<CitaDto> filtrarCitasPorEstadoYNombre(Long idMedico, CitaEntity.EstadoCita estado, String nombrePaciente) {
        List<CitaEntity> citas = citaRepository.buscarPorNombrePaciente(idMedico, nombrePaciente).stream()
                .filter(cita -> cita.getEstado().equals(estado))
                .collect(Collectors.toList());

        return citas.stream().map(cita -> {
            CitaDto dto = modelMapper.map(cita, CitaDto.class);
            dto.setNombrePaciente(cita.getPaciente().getNombre());
            dto.setNombreMedico(cita.getMedico().getNombre());
            return dto;
        }).collect(Collectors.toList());
    }

     public String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase();
    }

    public List<LocalDateTime> generarTodosLosEspacios(Long idMedico, List<HorarioMedicoDto> horarios) {
        List<LocalDateTime> espacios = new ArrayList<>();
        ZoneId zona = ZoneId.of("America/Costa_Rica");
        LocalDate fechaActual = LocalDate.now(zona);

        for (int i = 0; i < 3; i++) {
            LocalDate fecha = fechaActual.plusDays(i);
            String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "CR"));
            String diaNormalizado = normalizar(diaSemana);

            for (HorarioMedicoDto horario : horarios) {
                if (diaNormalizado.equals(horario.getDiaSemana().toLowerCase())) {
                    LocalTime inicio = LocalTime.parse(horario.getHoraInicio());
                    LocalTime fin = LocalTime.parse(horario.getHoraFin());
                    int duracion = horario.getTiempoCita();

                    ZonedDateTime current = ZonedDateTime.of(fecha, inicio, zona);
                    ZonedDateTime finZdt = ZonedDateTime.of(fecha, fin.minusMinutes(duracion), zona);

                    while (!current.isAfter(finZdt)) {
                        espacios.add(current.toLocalDateTime()); // guardas LocalDateTime plano, pero siempre en la zona correcta
                        current = current.plusMinutes(duracion);
                    }
                }
            }
        }
        return espacios;
    }


    public List<LocalDateTime> generarEspaciosDesdeFecha(Long idMedico, List<HorarioMedicoDto> horarios, LocalDate fechaInicio, int dias) {
        List<LocalDateTime> espacios = new ArrayList<>();

        for (int i = 0; i < dias; i++) {
            LocalDate fecha = fechaInicio.plusDays(i);
            String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "CR"));
            String diaNormalizado = normalizar(diaSemana);

            for (HorarioMedicoDto horario : horarios) {
                if (diaNormalizado.equals(horario.getDiaSemana().toLowerCase())) {
                    LocalTime inicio = LocalTime.parse(horario.getHoraInicio());
                    LocalTime fin = LocalTime.parse(horario.getHoraFin());
                    int duracion = horario.getTiempoCita();

                    LocalDateTime current = LocalDateTime.of(fecha, inicio);
                    while (!current.toLocalTime().isAfter(fin.minusMinutes(duracion))) {
                        boolean ocupado = citaRepository.existsByMedicoAndFechaHora(medicoRepository.getReferenceById(idMedico), current);
                        if (!ocupado) {
                            espacios.add(current);
                        }
                        current = current.plusMinutes(duracion);
                    }
                }
            }
        }

        return espacios;
    }

    @Override
    public List<CitaDto> filtrarCitasPorEstadoYNombreMedico(Long idPaciente, String estado, String nombreMedico) {
        CitaEntity.EstadoCita estadoEnum = CitaEntity.EstadoCita.valueOf(estado.toLowerCase());

        List<CitaEntity> citas = citaRepository.findByPacienteId(idPaciente).stream()
                .filter(cita ->
                        cita.getEstado().equals(estadoEnum) &&
                                cita.getMedico().getNombre().toLowerCase().contains(nombreMedico.toLowerCase())
                )
                .sorted((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()))
                .collect(Collectors.toList());

        return citas.stream()
                .map(cita -> new CitaDto(
                        cita.getId(),
                        cita.getPaciente().getId(),
                        cita.getPaciente().getNombre(),
                        cita.getMedico().getId(),
                        cita.getMedico().getNombre(),
                        cita.getFechaHora(),
                        cita.getEstado().name(),
                        cita.getNotas()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDateTime> generarTodosLosEspaciosExtendido(Long idMedico, List<HorarioMedicoDto> horarios) {
        // Generar para 14 días por ejemplo
        return generarEspaciosDesdeFecha(idMedico, horarios, LocalDate.now(), 14);
    }

}
