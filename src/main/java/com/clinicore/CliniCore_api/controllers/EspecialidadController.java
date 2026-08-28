package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.EspecialidadDTO;
import com.clinicore.CliniCore_api.interfaces.IEspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class EspecialidadController {
    private final IEspecialidadService especialidadService;

    //creamos el ept para obtener todas las especialidades

    @GetMapping("/especialidades")
    public ResponseEntity<List<EspecialidadDTO>> getAll(){
        return ResponseEntity.ok(especialidadService.findAll());
    }

    //Creamos el ept para obtener una especialidad
    @GetMapping("/especialidades/{id}")
    public ResponseEntity<EspecialidadDTO> getById(@PathVariable Integer id){
        return ResponseEntity.ok(especialidadService.findById(id));
    }

    //Creamos el ept para crear una especialidad
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/especialidades")
    public ResponseEntity<?> create(@RequestBody EspecialidadDTO dto){
        //Creamos un map para restructurar la repuesta
        Map<String, Object> response = new HashMap<>();
        EspecialidadDTO especialidadSave = especialidadService.saveOrUpdate(dto);
        //Construimos la respuesta
        response.put("message", "Especialidad registrada correctamente.");
        response.put("especialidad", especialidadSave);
        //Retornamos la respuesta
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Creamos el ept para actualizar una especialidad
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/especialidades/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody EspecialidadDTO dto){
        //Creamos el map para restructurar la respuesta
        Map<String, Object> response = new HashMap<>();
        dto.setId(id);
        EspecialidadDTO actualizada = especialidadService.saveOrUpdate(dto);
        //Construimos la respuesta
        response.put("message", "Especialidad actualizada correctamente.");
        response.put("especialidad", actualizada);
        //Retornamos la respuesta
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Creamos el ept para eliminar una especialidad
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/especialidades/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        especialidadService.delete(id);
        //Creamos el map para restructurar la consulta
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialidad eliminada correctamente.");
        //Retornamos la respuesta
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

}
