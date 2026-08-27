package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.auth.*;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);

    LoginResponseDTO registrarPaciente(RegistroPacienteDTO dto);

    LoginResponseDTO registrarDoctor(RegistroDoctorDTO dto);

    LoginResponseDTO registrarPersonal(RegistroPersonalDTO dto);
}