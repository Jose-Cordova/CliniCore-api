package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.UsuarioDTO;
import com.clinicore.CliniCore_api.dto.UsuarioEstadoDTO; // nuevo DTO
import com.clinicore.CliniCore_api.dto.auth.*;
import com.clinicore.CliniCore_api.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // ==================== AUTH ====================

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/registro-paciente")
    public ResponseEntity<LoginResponseDTO> registrarPaciente(@RequestBody RegistroPacienteDTO dto) {
        LoginResponseDTO response = authService.registrarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/registro-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarDoctor(@RequestBody RegistroDoctorDTO dto) {
        LoginResponseDTO response = authService.registrarDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/registro-personal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarPersonal(@RequestBody RegistroPersonalDTO dto) {
        LoginResponseDTO response = authService.registrarPersonal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/registro-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarAdmin(@RequestBody RegistroAdminDTO dto) {
        LoginResponseDTO response = authService.registrarAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/cambiar-contrasenia")
    public ResponseEntity<LoginResponseDTO> cambiarContrasenia(@RequestBody CambioContrasenaDTO dto) {
        LoginResponseDTO response = authService.cambiarContrasenia(dto.getNuevaContrasenia());
        return ResponseEntity.ok(response);
    }

    // ==================== USUARIOS ====================

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @PatchMapping("/usuarios/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstadoUsuario(
            @PathVariable Integer id,
            @RequestBody UsuarioEstadoDTO dto) {
        authService.cambiarEstadoUsuario(id, dto.isEstado());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Estado del usuario actualizado correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuarios/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> resetearContrasenia(@PathVariable Integer id) {
        LoginResponseDTO response = authService.resetearContrasenia(id);
        return ResponseEntity.ok(response);
    }
}