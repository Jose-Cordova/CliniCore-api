package com.clinicore.CliniCore_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Si no hay token, continuamos sin autenticar
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // quitar "Bearer "

        try {
            Claims claims = jwtService.validarYExtraerClaims(token);

            // Extraer datos del token
            String email = claims.getSubject(); // en nuestro caso es el email
            String nombre = claims.get("nombre", String.class);
            String tipo = claims.get("tipo", String.class);
            Integer pacienteId = claims.get("pacienteId", Integer.class);
            Integer doctorId = claims.get("doctorId", Integer.class);
            String rol = claims.get("rol", String.class);

            // Construir el usuario autenticado
            AuthenticatedUser usuarioAutenticado = new AuthenticatedUser(
                    email, nombre, tipo, pacienteId, doctorId, rol
            );

            // Crear la autoridad a partir del rol
            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            // Crear el objeto Authentication
            var authentication = new UsernamePasswordAuthenticationToken(
                    usuarioAutenticado,
                    null,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Guardar en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            // Token inválido o expirado: limpiamos el contexto y dejamos pasar
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}