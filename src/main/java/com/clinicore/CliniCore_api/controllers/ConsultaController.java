package com.clinicore.CliniCore_api.controllers;


import com.clinicore.CliniCore_api.dto.ConsultaDTO;
import com.clinicore.CliniCore_api.entities.Cita;
import com.clinicore.CliniCore_api.entities.Consulta;
import com.clinicore.CliniCore_api.enums.EstadoCita;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IConsultaService;
import com.clinicore.CliniCore_api.mappers.ConsultaMapper;
import com.clinicore.CliniCore_api.repository.CitaRepository;
import com.clinicore.CliniCore_api.repository.ConsultaRepository;
import com.clinicore.CliniCore_api.repository.DoctorRepository;
import com.clinicore.CliniCore_api.security.AuthenticatedUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final IConsultaService consultaService;

    //enponint para la emfermera
    // ella registra los primeros campos para la consulta
    @PostMapping("/tiraje")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<?> registrarTiraje(@RequestBody ConsultaDTO tirajeDTO) {
        Map<String, Object> response = new HashMap<>();
        ConsultaDTO nuevoTiraje = consultaService.registrarTiraje(tirajeDTO);

        response.put("message", "Signos vitales y tiraje resgistrados exitosamente, El paciente está en la lista de espera.");
        response.put("consulta", nuevoTiraje);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/cita/{citaId}/finalizar")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> finalizarConsultaDoctor(
            @PathVariable Integer citaId,
            @RequestBody ConsultaDTO consultaDTO,
            @AuthenticationPrincipal AuthenticatedUser usuario) {

        consultaDTO.setDoctorId(usuario.doctorId());

        Map<String, Object> response = new HashMap<>();
        ConsultaDTO consultaFinalizada = consultaService.finalizarConsultaDoctor(citaId, consultaDTO);

        response.put("message", "Consulta médica completada con éxito y expediente actualizado.");
        response.put("consulta", consultaFinalizada);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Edición exclusiva para la Enfermera (Si se equivocó en el peso, presión, etc.)
    @PutMapping("/{id}/tiraje/editar")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<?> editarTirajeEnfermera(
            @PathVariable Integer id,
            @RequestBody ConsultaDTO tirajeDTO) {
        Map<String, Object> response = new HashMap<>();
        ConsultaDTO tirajeEditado = consultaService.editarTirajeEnfermera(id, tirajeDTO);

        response.put("message", "Los signos vitales del tiraje han sido corregidos correctamente.");
        response.put("consulta", tirajeEditado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Edición exclusiva para el Doctor (Si necesita corregir su diagnóstico o receta)
    @PutMapping("/{id}/doctor/editar")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> editarConsultaDoctor(
            @PathVariable Integer id,
            @RequestBody ConsultaDTO consultaDTO) {
        Map<String, Object> response = new HashMap<>();
        ConsultaDTO consultaEditada = consultaService.editarConsultaDoctor(id, consultaDTO);

        response.put("message", "El diagnóstico y tratamiento médico han sido actualizados.");
        response.put("consulta", consultaEditada);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Ver expediente completo (Disponible para el Paciente, Doctor y Administrador)
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<ConsultaDTO>> getExpedientePaciente(@PathVariable Integer pacienteId) {
        List<ConsultaDTO> historial = consultaService.findByPacienteId(pacienteId);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }

    // Historial de todas las consultas realizadas por un Doctor en específico
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<ConsultaDTO>> getConsultasPorDoctor(@PathVariable Integer doctorId) {
        List<ConsultaDTO> consultas = consultaService.findByDoctorId(doctorId);
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }

}
