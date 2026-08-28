package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.UsuarioDTO;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * Endpoint público para iniciar sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para registro de pacientes.
     */
    @PostMapping("/registro-paciente")
    public ResponseEntity<LoginResponseDTO> registrarPaciente(@RequestBody RegistroPacienteDTO dto) {
        LoginResponseDTO response = authService.registrarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint protegido: solo ADMIN puede registrar doctores.
     */
    @PostMapping("/registro-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarDoctor(@RequestBody RegistroDoctorDTO dto) {
        LoginResponseDTO response = authService.registrarDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint protegido: solo ADMIN puede registrar personal.
     */
    @PostMapping("/registro-personal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarPersonal(@RequestBody RegistroPersonalDTO dto) {
        LoginResponseDTO response = authService.registrarPersonal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint autenticado para que cualquier usuario cambie su contraseña.
     * Se utiliza principalmente cuando debeCambiarContrasenia es true.
     */
    @PostMapping("/cambiar-contrasenia")
    public ResponseEntity<LoginResponseDTO> cambiarContrasenia(@RequestBody CambioContrasenaDTO dto) {
        LoginResponseDTO response = authService.cambiarContrasenia(dto.getNuevaContrasenia());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    @PostMapping("/registro-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarAdmin(@RequestBody RegistroAdminDTO dto) {
        LoginResponseDTO response = authService.registrarAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}