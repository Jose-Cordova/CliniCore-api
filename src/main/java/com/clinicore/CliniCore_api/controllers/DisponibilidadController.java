package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.DisponibilidadDTO;
import com.clinicore.CliniCore_api.dto.GenerarDisponibilidadRequest;
import com.clinicore.CliniCore_api.interfaces.IDisponibilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/disponibilidades")
@RequiredArgsConstructor
public class DisponibilidadController {
    private final IDisponibilidadService disponibilidadService;

    //Generar slots de disponibilidad
    @PostMapping("/generar")
    public ResponseEntity<?> generarSlot(@RequestBody GenerarDisponibilidadRequest request){
        int slotsGenerados = disponibilidadService.generarDisponibilidadesPorRango(
                request.getDoctorId(),
                request.getFechaInicio(),
                request.getFechaFin()
        );
        Map<String, Object> response = new HashMap<>();
        if (slotsGenerados > 0) {
            response.put("message", "Disponibilidades generadas correctamente.");
        } else {
            response.put("message", "No se generaron nuevas disponibilidades. Los días solicitados ya tienen slots creados.");
        }
        response.put("slotsGenerados", slotsGenerados);
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Obtener slots disponibles por doctor y fecha
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DisponibilidadDTO>> getPorDoctorFecha(
            @PathVariable Integer doctorId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(disponibilidadService.obtenerDisponiblesPorDoctorFecha(doctorId, fecha));
    }

    //Obtener slots disponibles por especialidad
    @GetMapping("/especialidad/{especialidadId}")
    public  ResponseEntity<List<DisponibilidadDTO>> getPorEspecialidadFecha(
            @PathVariable Integer especialidadId,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(disponibilidadService.obtenerDisponiblesPorEspecialidadFecha(especialidadId, fecha));
    }
}
