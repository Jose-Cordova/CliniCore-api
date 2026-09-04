package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.CitaDTO;
import com.clinicore.CliniCore_api.enums.EstadoCita;
import com.clinicore.CliniCore_api.interfaces.ICitaService;
import com.clinicore.CliniCore_api.security.AuthenticatedUser;
import com.clinicore.CliniCore_api.services.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/citas")
@RequiredArgsConstructor

public class CitaController {

    private final ICitaService citaService;

    //Obtenemos todas las citas
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PERSONAL')")
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        return ResponseEntity.ok(citaService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PERSONAL', 'PACIENTE')")
    public ResponseEntity<?> getCitaById(@PathVariable Integer id) {
       return ResponseEntity.ok(citaService.findById(id));
    }


    //Obtenemos el historial (las citas de un paciente) y el paciente obtiene sus citas
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ADMIN')")
    public  ResponseEntity<List<CitaDTO>> getCitasByPaciente(@PathVariable Integer pacienteId) {
        return ResponseEntity.ok(citaService.findByPacienteId(pacienteId));
    }


    //Obtenemos las citas asignadad a un doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<CitaDTO>> getCitasByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(citaService.findByDoctorId(doctorId));
    }

    @PostMapping("/agendar-cita")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> agendarCita(
            @RequestBody CitaDTO requestDTO,
            @AuthenticationPrincipal AuthenticatedUser usuario) {

        requestDTO.setPacienteId(usuario.pacienteId());

        Map<String, Object> response = new HashMap<>();
        CitaDTO nuevaCita = citaService.agendarCita(requestDTO);

        response.put("message", "Cita agendada correctamente...!");
        response.put("cita", nuevaCita);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Esto existía antes y ya no está:
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        CitaDTO citaCancelada = citaService.cancelarCita(id);

        response.put("message", "Cita cancelada correctamente.");
        response.put("cita", citaCancelada);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/reasignar/{nuevoHorarioId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> reasignarCita(
            @PathVariable Integer id,
            @PathVariable Integer nuevoHorarioId) {
        Map<String, Object> response = new HashMap<>();

        //Obtenemos la cita actualizada
        CitaDTO citaReasignada = citaService.reasignarCita(id, nuevoHorarioId);

        //Obtenemos el nombre del doctor
        String nombreDoctor = citaReasignada.getDoctorNombre();

        response.put("message", "La cita reasignada correctamente a el/la Dr(a). " + nombreDoctor + ".");
        response.put("cita", citaReasignada);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoCita nuevoEstado) {
        Map<String, Object> response = new HashMap<>();
        CitaDTO citaActualizada = citaService.cambiarEstado(id, nuevoEstado);

        response.put("message", "Estado de la cita actualizado a " + nuevoEstado + " correctamente.");
        response.put("cita", citaActualizada);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
