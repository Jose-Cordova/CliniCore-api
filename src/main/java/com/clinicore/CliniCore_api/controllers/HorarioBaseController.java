package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.HorarioBaseDTO;
import com.clinicore.CliniCore_api.interfaces.IHorarioBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/horarios-base")
@RequiredArgsConstructor
public class HorarioBaseController {
    private final IHorarioBaseService service;

    //Obtener horarios de un doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<HorarioBaseDTO>> getByDoctorId(@PathVariable Integer doctorId){
        return ResponseEntity.ok(service.findByDoctorId(doctorId));
    }

    //Guardar/Actualizar horarios de un doctor
    @PutMapping("/doctor/{doctorId}")
    public ResponseEntity<?> guardar(@PathVariable Integer doctorId, @RequestBody List<HorarioBaseDTO> horarios){
        List<HorarioBaseDTO> guardados = service.guardarHorarios(doctorId, horarios);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Horarios actualizados correctamente.");
        response.put("horarios", guardados);
        return ResponseEntity.ok(response);
    }

}
