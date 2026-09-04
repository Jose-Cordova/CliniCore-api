package com.clinicore.CliniCore_api.controllers;
import com.clinicore.CliniCore_api.dto.PacienteDTO;

import com.clinicore.CliniCore_api.interfaces.IPacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final IPacienteService pacienteService;
    //Obetener por medio del metodo GET  todos los pacientes
    @GetMapping

    public ResponseEntity<List<PacienteDTO>> getAll() {
        return ResponseEntity.ok(pacienteService.findAll());
    }
    // Obtener paciente por ID
    @GetMapping("/{id}")
    public final ResponseEntity<PacienteDTO> getById(@PathVariable Integer id ){
        return ResponseEntity.ok(pacienteService.findById(id));
    }
    // Obtener paciente por código de expediente
    @GetMapping("/expediente/{codigoExpediente}")
    public ResponseEntity<PacienteDTO> getByCodigoExpediente(@PathVariable String codigoExpediente) {
        return ResponseEntity.ok(pacienteService.findByCodigoExpediente(codigoExpediente));
    }
    // Actualizar paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody PacienteDTO dto) {
        Map<String, Object> response = new HashMap<>();
        dto.setId(id);
        PacienteDTO pacienteActualizado = pacienteService.update(dto);
        response.put("message", "Paciente actualizado correctamente.");
        response.put("paciente", pacienteActualizado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Cambiar estado de archivado del expediente (true/false)
    @PatchMapping("/{id}/archivado")
    public ResponseEntity<?> cambiarEstadoArchivado(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        Map<String, Object> response = new HashMap<>();
        Boolean archivado = body.get("archivado");
        if (archivado == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "El campo 'archivado' es obligatorio."));
        }
        pacienteService.cambiarEstadoArchivado(id, archivado);
        response.put("message", "Estado de archivado actualizado correctamente.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


