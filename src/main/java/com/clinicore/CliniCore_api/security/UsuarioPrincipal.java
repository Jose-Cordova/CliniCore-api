package com.clinicore.CliniCore_api.security;

import com.clinicore.CliniCore_api.entities.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Integer id;
    private final String username; // email usado para login
    private final String password;
    private final Boolean activo;
    private final String nombre;
    private final String tipo; // "PACIENTE", "DOCTOR" o "ADMIN"
    private final Integer pacienteId;
    private final Integer doctorId;
    private final String rol;

    public UsuarioPrincipal(
            Usuario usuario,
            String nombre,
            String tipo,
            Integer pacienteId,
            Integer doctorId,
            String rol) {
        this.id = usuario.getId();
        this.username = usuario.getEmail();
        this.password = usuario.getContrasenia();
        this.activo = usuario.isEstado();
        this.nombre = nombre;
        this.tipo = tipo;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.rol = rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(activo);
    }
}