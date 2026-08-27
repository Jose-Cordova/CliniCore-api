package com.clinicore.CliniCore_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando el usuario SÍ tiene permiso para usar el endpoint (eso
 * ya lo filtró @PreAuthorize por rol), pero el recurso puntual que pide
 * no le pertenece - ej. un cliente pidiendo el vehículo de otro cliente.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccesoNoAutorizadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccesoNoAutorizadoException(String message) {
        super(message);
    }
}