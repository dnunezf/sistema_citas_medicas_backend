package com.example.sistema_citas_medicas_backend.presentacion.controllers;


import com.example.sistema_citas_medicas_backend.dto.CitaDto;
import com.example.sistema_citas_medicas_backend.dto.HorarioMedicoDto;
import com.example.sistema_citas_medicas_backend.dto.MedicoDto;
import com.example.sistema_citas_medicas_backend.servicios.CitaService;
import com.example.sistema_citas_medicas_backend.servicios.HorarioMedicoService;
import com.example.sistema_citas_medicas_backend.servicios.MedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    private final MedicoService medicoService;
    private final HorarioMedicoService horarioMedicoService;
    private final CitaService citaService;

    public DashboardController(MedicoService medicoService,
                               HorarioMedicoService horarioMedicoService,
                               CitaService citaService) {
        this.medicoService = medicoService;
        this.horarioMedicoService = horarioMedicoService;
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerDashboard(
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String localidad) {

        List<MedicoDto> medicos;

        if ((especialidad == null || especialidad.isBlank()) &&
                (localidad == null || localidad.isBlank())) {
            medicos = medicoService.obtenerMedicos().stream()
                    .filter(m -> "aprobado".equalsIgnoreCase(m.getEstadoAprobacion()))
                    .collect(Collectors.toList());
        } else {
            medicos = medicoService.buscarPorEspecialidadYUbicacion(especialidad, localidad).stream()
                    .filter(m -> "aprobado".equalsIgnoreCase(m.getEstadoAprobacion()))
                    .collect(Collectors.toList());
        }

        Map<Long, Map<String, List<String>>> espaciosAgrupadosPorFecha = new HashMap<>();
        Map<Long, List<String>> horasOcupadasPorMedico = new HashMap<>();


        for (MedicoDto medico : medicos) {
            List<HorarioMedicoDto> horarios = horarioMedicoService.obtenerHorariosPorMedico(medico.getId());
            List<LocalDateTime> espacios = citaService.generarTodosLosEspacios(medico.getId(), horarios);

            ZoneId zona = ZoneId.of("America/Costa_Rica"); // o systemDefault()
            LocalDate hoy = LocalDate.now(zona);
            LocalDate limite = hoy.plusDays(2);

// Ajustar cada LocalDateTime a la zona local antes de filtrar

            List<LocalDateTime> espaciosFiltrados = espacios.stream()
                    .filter(d -> {
                        // OJO: Aquí d debería estar en la zona correcta o convertirla
                        LocalDate fecha = d.toLocalDate();
                        return !fecha.isBefore(hoy) && !fecha.isAfter(limite);
                    })
                    .collect(Collectors.toList());


            // Agrupar espacios disponibles por fecha (yyyy-MM-dd)
            Map<String, List<String>> agrupados = espaciosFiltrados.stream()
                    .collect(Collectors.groupingBy(
                            d -> d.toLocalDate().toString(),  // YYYY-MM-DD
                            LinkedHashMap::new,
                            Collectors.mapping(LocalDateTime::toString, Collectors.toList())
                    ));

            espaciosAgrupadosPorFecha.put(medico.getId(), agrupados);

            // Obtener horas ocupadas igual que antes
            List<CitaDto> citas = citaService.obtenerCitasPorMedico(medico.getId());
            List<String> ocupadas = citas.stream()
                    .map(c -> c.getFechaHora().toString())
                    .collect(Collectors.toList());

            horasOcupadasPorMedico.put(medico.getId(), ocupadas);
        }

        // Construir y devolver respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("medicos", medicos);
        response.put("espaciosAgrupados", espaciosAgrupadosPorFecha);
        response.put("horasOcupadas", horasOcupadasPorMedico);
        response.put("especialidades", medicoService.obtenerEspecialidades());

        return ResponseEntity.ok(response);
    }
}
