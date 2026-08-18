package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.DoctorDTO;
import com.clinicore.CliniCore_api.interfaces.IDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class DoctorController {
    private final IDoctorService doctorService;

    //Obtenemos todos los doctores
    @GetMapping("/doctores")
    public ResponseEntity<List<DoctorDTO>> getAll(){
        return ResponseEntity.ok(doctorService.findAll());
    }

    //Obtenemos un doctor por id
    @GetMapping("/doctores/{id}")
    public ResponseEntity<DoctorDTO> getById(@PathVariable Integer id){
        return ResponseEntity.ok(doctorService.findById(id));
    }

    //Creamos un doctor
    @PostMapping("/doctores")
    public ResponseEntity<?> create(@RequestBody DoctorDTO dto){
        Map<String, Object> response = new HashMap<>();
        DoctorDTO doctorSave = doctorService.saveOrUpdate(dto);
        response.put("message", "Doctor registrado correctamente.");
        response.put("doctor", doctorSave);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Actualizamos un doctor
    @PutMapping("/doctores/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DoctorDTO dto){
        Map<String, Object> response = new HashMap<>();
        dto.setId(id);
        DoctorDTO doctorActualizado = doctorService.saveOrUpdate(dto);
        response.put("message", "Doctor actualizado correctamente.");
        response.put("doctor", doctorActualizado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Eliminas un doctor
    @DeleteMapping("/doctores/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        doctorService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Doctor eliminado correctamente.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
