package com.clinicore.CliniCore_api.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //Maneja errores de validacion de los campos en caso de usar @NotBlank, @Size entre otras
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex){
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->{
            errors.put(err.getField(), err.getDefaultMessage());
        });
        response.put("message", "Error de validación en los campos enviados.");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Metodo para definir recursos no encontrados
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handlerResourceNotFound(ResourceNotFoundException ex){
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //Metodo para manejo de peticiones incorrectas o logica de negocio fallida
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handlerBadRequest(BadRequestException ex){
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Metodo para el manejo de errores de la base de datos
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handlerDbError(DataAccessException ex){
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Error al realizar la operacion en la base de datos.");
        response.put("errors", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Manejo de excepciones de conflicto
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictRequest(ConflictException ex){
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
