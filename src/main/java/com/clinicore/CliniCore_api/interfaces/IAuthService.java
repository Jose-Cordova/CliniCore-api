package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.auth.LoginRequestDTO;
import com.clinicore.CliniCore_api.dto.auth.LoginResponseDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroDoctorDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroPacienteDTO;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);

    LoginResponseDTO registrarPaciente(RegistroPacienteDTO dto);

    LoginResponseDTO registrarDoctor(RegistroDoctorDTO dto);
}