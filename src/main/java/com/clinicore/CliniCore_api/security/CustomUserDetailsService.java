package com.clinicore.CliniCore_api.security;

import com.clinicore.CliniCore_api.entities.Doctor;
import com.clinicore.CliniCore_api.entities.Paciente;
import com.clinicore.CliniCore_api.entities.Usuario;
import com.clinicore.CliniCore_api.entities.UsuarioRole;
import com.clinicore.CliniCore_api.repository.DoctorRepository;
import com.clinicore.CliniCore_api.repository.PacienteRepository;
import com.clinicore.CliniCore_api.repository.UsuarioRepository;
import com.clinicore.CliniCore_api.repository.UsuarioRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));

        // 2. Obtener su rol
        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));

        String rol = usuarioRole.getRole().getNombre();
        boolean debeCambiar = usuario.isDebeCambiarContrasenia();

        // 3. Ver si es doctor
        Optional<Doctor> doctorOpt = doctorRepository.findByUsuario_Id(usuario.getId());
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            return new UsuarioPrincipal(
                    usuario,
                    doctor.getNombre() + " " + doctor.getApellido(),
                    "DOCTOR",
                    null,
                    doctor.getId(),
                    rol,
                    debeCambiar
            );
        }

        // 4. Ver si es paciente
        Optional<Paciente> pacienteOpt = pacienteRepository.findByUsuario_Id(usuario.getId());
        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            return new UsuarioPrincipal(
                    usuario,
                    paciente.getNombre() + " " + paciente.getApellido(),
                    "PACIENTE",
                    paciente.getId(),
                    null,
                    rol,
                    debeCambiar
            );
        }

        // 5. Si no es doctor ni paciente, es ADMIN o PERSONAL
        return new UsuarioPrincipal(
                usuario,
                usuario.getEmail(),
                rol,
                null,
                null,
                rol,
                debeCambiar
        );
    }
}