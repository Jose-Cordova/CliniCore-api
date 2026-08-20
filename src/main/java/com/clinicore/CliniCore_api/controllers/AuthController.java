package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.auth.LoginRequestDTO;
import com.clinicore.CliniCore_api.dto.auth.LoginResponseDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroDoctorDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroPacienteDTO;
import com.clinicore.CliniCore_api.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * Endpoint público para iniciar sesión.
     * Devuelve token JWT y datos del usuario autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para que un paciente se registre por sí mismo.
     * Crea el usuario con rol PACIENTE, su expediente (entidad Paciente)
     * y devuelve el token para que quede logueado.
     */
    @PostMapping("/registro-paciente")
    public ResponseEntity<LoginResponseDTO> registrarPaciente(@RequestBody RegistroPacienteDTO dto) {
        LoginResponseDTO response = authService.registrarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint protegido: solo un ADMIN puede registrar un doctor.
     * La autenticación ya viene exigida por SecurityConfig (anyRequest().authenticated())
     * y @PreAuthorize refuerza que el rol sea ADMIN.
     */
    @PostMapping("/registro-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginResponseDTO> registrarDoctor(@RequestBody RegistroDoctorDTO dto) {
        LoginResponseDTO response = authService.registrarDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}