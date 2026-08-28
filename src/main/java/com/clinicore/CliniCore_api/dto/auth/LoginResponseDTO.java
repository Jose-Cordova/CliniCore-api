package com.clinicore.CliniCore_api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String email;
    private String nombre;
    private String tipo;
    private String role;
    private boolean debeCambiarContrasenia;
    private String contraseniaTemporal; // solo para registros de doctor/personal
}