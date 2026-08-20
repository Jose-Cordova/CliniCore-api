package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.auth.LoginRequestDTO;
import com.clinicore.CliniCore_api.dto.auth.LoginResponseDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroDoctorDTO;
import com.clinicore.CliniCore_api.dto.auth.RegistroPacienteDTO;
import com.clinicore.CliniCore_api.entities.*;
import com.clinicore.CliniCore_api.enums.EstadoExpediente;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ConflictException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IAuthService;
import com.clinicore.CliniCore_api.repository.*;
import com.clinicore.CliniCore_api.security.JwtService;
import com.clinicore.CliniCore_api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final String ROLE_PACIENTE = "PACIENTE";
    private static final String ROLE_DOCTOR = "DOCTOR";

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final PacienteRepository pacienteRepository;
    private final DoctorRepository doctorRepository;
    private final RoleRepository roleRepository;
    private final EspecialidadRepository especialidadRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Usuario o contraseña incorrectos");
        }

        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        String token = jwtService.generarToken(principal);

        return new LoginResponseDTO(
                token,
                principal.getUsername(),
                principal.getNombre(),
                principal.getTipo(),
                principal.getRol()
        );
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarPaciente(RegistroPacienteDTO dto) {
        validarCredencialesNuevas(dto.getEmail(), dto.getPassword());

        // 1. Crear paciente
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setDui(dto.getDui());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setGenero(dto.getGenero());
        paciente.setDireccion(dto.getDireccion());
        paciente.setTelefono(dto.getTelefono());
        paciente.setAlergiaIntolerancia(dto.getAlergiaIntolerancia()); // corregir nombre si es necesario
        paciente.setFechaRegistro(LocalDate.now());
        paciente.setCodigoExpediente(generarCodigoExpediente());
        paciente.setEstado(EstadoExpediente.ACTIVO); // asumiendo que el enum tiene ACTIVO

        paciente = pacienteRepository.save(paciente);

        // 2. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(true);
        usuario = usuarioRepository.save(usuario);

        // 3. Asociar usuario al paciente
        paciente.setUsuario(usuario);
        pacienteRepository.save(paciente);

        // 4. Asignar rol PACIENTE
        asignarRole(usuario, ROLE_PACIENTE);

        // 5. Generar token de respuesta
        return generarRespuestaConToken(usuario, paciente.getNombre() + " " + paciente.getApellido(),
                "PACIENTE", ROLE_PACIENTE);
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarDoctor(RegistroDoctorDTO dto) {
        validarCredencialesNuevas(dto.getEmail(), dto.getPassword());

        // 1. Validar especialidad
        Especialidad especialidad = especialidadRepository.findById(dto.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        // 2. Crear doctor
        Doctor doctor = new Doctor();
        doctor.setNombre(dto.getNombre());
        doctor.setApellido(dto.getApellido());
        doctor.setEmail(dto.getEmail());
        doctor.setTelefono(dto.getTelefono());
        doctor.setCodigo(dto.getCodigo());
        doctor.setEspecialidad(especialidad);
        doctor = doctorRepository.save(doctor);

        // 3. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(true);
        usuario = usuarioRepository.save(usuario);

        // 4. Asociar usuario al doctor
        doctor.setUsuario(usuario);
        doctorRepository.save(doctor);

        // 5. Asignar rol DOCTOR
        asignarRole(usuario, ROLE_DOCTOR);

        // 6. Generar token de respuesta
        return generarRespuestaConToken(usuario, doctor.getNombre() + " " + doctor.getApellido(),
                "DOCTOR", ROLE_DOCTOR);
    }

    private void validarCredencialesNuevas(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }
        if (password == null || password.length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Ya existe un usuario con el email '" + email + "'");
        }
    }

    private void asignarRole(Usuario usuario, String nombreRole) {
        Role role = roleRepository.findByNombre(nombreRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el rol '" + nombreRole + "' - debe crearse primero en la tabla roles"));

        UsuarioRole usuarioRole = new UsuarioRole();
        usuarioRole.setUsuario(usuario);
        usuarioRole.setRole(role);
        usuarioRoleRepository.save(usuarioRole);
    }

    private LoginResponseDTO generarRespuestaConToken(Usuario usuario, String nombre, String tipo, String rol) {
        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                nombre,
                tipo,
                obtenerPacienteId(usuario),
                obtenerDoctorId(usuario),
                rol
        );
        String token = jwtService.generarToken(principal);
        return new LoginResponseDTO(token, usuario.getEmail(), nombre, tipo, rol);
    }

    private Integer obtenerPacienteId(Usuario usuario) {
        return pacienteRepository.findByUsuario_Id(usuario.getId())
                .map(Paciente::getId)
                .orElse(null);
    }

    private Integer obtenerDoctorId(Usuario usuario) {
        return doctorRepository.findByUsuario_Id(usuario.getId())
                .map(Doctor::getId)
                .orElse(null);
    }

    private String generarCodigoExpediente() {
        YearMonth mesActual = YearMonth.now();
        String prefijo = "C" + String.format("%02d", mesActual.getMonthValue()) + "-";

        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();
        long cantidad = pacienteRepository.countByFechaRegistroBetween(inicio, fin);

        return prefijo + String.format("%03d", cantidad + 1);
    }
}