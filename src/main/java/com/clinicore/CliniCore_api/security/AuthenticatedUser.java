package com.clinicore.CliniCore_api.security;

/**
 * Se construye ÚNICAMENTE a partir de los claims del token
 * (JwtAuthenticationFilter), nunca consultando la base de datos.
 */
public record AuthenticatedUser(
        String email,
        String nombre,
        String tipo,
        Integer pacienteId,
        Integer doctorId,
        String rol
) {
}