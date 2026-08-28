package com.clinicore.CliniCore_api.security;

public record AuthenticatedUser(
        Integer id,
        String email,
        String nombre,
        String tipo,
        Integer pacienteId,
        Integer doctorId,
        String rol,
        boolean debeCambiarContrasenia
) {}