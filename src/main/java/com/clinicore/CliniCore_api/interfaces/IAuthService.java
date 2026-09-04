package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.UsuarioDTO;
import com.clinicore.CliniCore_api.dto.auth.*;

import java.util.List;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);

    LoginResponseDTO registrarPaciente(RegistroPacienteDTO dto);

    LoginResponseDTO registrarDoctor(RegistroDoctorDTO dto);

    LoginResponseDTO registrarPersonal(RegistroPersonalDTO dto);

    LoginResponseDTO cambiarContrasenia(String nuevaContrasenia);
    LoginResponseDTO resetearContrasenia(Integer usuarioId);
    void cambiarEstadoUsuario(Integer usuarioId, boolean nuevoEstado);

    List<UsuarioDTO> listarUsuarios();
    LoginResponseDTO registrarAdmin(RegistroAdminDTO dto);

}