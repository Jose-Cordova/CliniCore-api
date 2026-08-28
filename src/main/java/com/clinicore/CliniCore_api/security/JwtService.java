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
     */
    public String generarToken(UsuarioPrincipal principal) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("id", principal.getId())
                .claim("nombre", principal.getNombre())
                .claim("tipo", principal.getTipo())
                .claim("pacienteId", principal.getPacienteId())
                .claim("doctorId", principal.getDoctorId())
                .claim("rol", principal.getRol())
                .claim("debeCambiarContrasenia", principal.isDebeCambiarContrasenia())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerLlave())
                .compact();
    }

    /**
     * Valida la firma y la expiración del token.
     */
    public Claims validarYExtraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerLlave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}