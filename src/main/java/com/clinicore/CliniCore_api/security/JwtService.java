package com.clinicore.CliniCore_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey obtenerLlave() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT con los datos que luego viajarán en cada petición.
     * El subject será el email del usuario.
     */
    public String generarToken(UsuarioPrincipal principal) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(principal.getUsername())               // email
                .claim("id", principal.getId())                 // id usuario
                .claim("nombre", principal.getNombre())         // nombre real
                .claim("tipo", principal.getTipo())             // DOCTOR, PACIENTE, ADMIN...
                .claim("pacienteId", principal.getPacienteId()) // id de paciente (si aplica)
                .claim("doctorId", principal.getDoctorId())     // id de doctor (si aplica)
                .claim("rol", principal.getRol())               // rol principal
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerLlave())                       // firma HS256
                .compact();
    }

    /**
     * Valida la firma y la expiración del token.
     * Lanza io.jsonwebtoken.JwtException si el token es inválido o expiró.
     */
    public Claims validarYExtraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerLlave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}