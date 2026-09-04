package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.UsuarioDTO;
import com.clinicore.CliniCore_api.dto.auth.*;
import com.clinicore.CliniCore_api.entities.*;

import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ConflictException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IAuthService;
import com.clinicore.CliniCore_api.repository.*;
import com.clinicore.CliniCore_api.security.AuthenticatedUser;
import com.clinicore.CliniCore_api.security.JwtService;
import com.clinicore.CliniCore_api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final String ROLE_PACIENTE = "PACIENTE";
    private static final String ROLE_DOCTOR = "DOCTOR";
    private static final String ROLE_PERSONAL = "PERSONAL";
    private static final String ROLE_ADMIN = "ADMIN";

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
                principal.getRol(),
                principal.isDebeCambiarContrasenia(),
                null
        );
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarPaciente(RegistroPacienteDTO dto) {
        validarCredencialesNuevas(dto.getEmail(), dto.getPassword());

        // Normalización de valores para validación y guardado
        String telefonoLimpio = dto.getTelefono() != null ? dto.getTelefono().replaceAll("\\D", "") : "";
        String duiLimpio = dto.getDui() != null ? dto.getDui().replaceAll("\\D", "") : "";

        if (pacienteRepository.existsByDui(duiLimpio)) {
            throw new ConflictException("Ya existe un paciente con el DUI '" + dto.getDui() + "'");
        }
        if (pacienteRepository.existsByTelefono(telefonoLimpio)) {
            throw new ConflictException("Ya existe un paciente con el teléfono '" + dto.getTelefono() + "'");
        }

        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setDui(duiLimpio);
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setGenero(dto.getGenero());
        paciente.setDireccion(dto.getDireccion());
        paciente.setTelefono(telefonoLimpio);
        paciente.setAlergiaIntolerancia(dto.getAlergiaIntolerancia());
        paciente.setFechaRegistro(LocalDate.now());
        paciente.setCodigoExpediente(generarCodigoExpediente());
        paciente.setArchivado(false);
        paciente = pacienteRepository.save(paciente);

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(true);
        usuario.setDebeCambiarContrasenia(false);
        usuario = usuarioRepository.save(usuario);

        paciente.setUsuario(usuario);
        pacienteRepository.save(paciente);

        asignarRole(usuario, ROLE_PACIENTE);

        return generarRespuestaConToken(usuario, paciente.getNombre() + " " + paciente.getApellido(),
                "PACIENTE", ROLE_PACIENTE, false, null);
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarDoctor(RegistroDoctorDTO dto) {
        validarEmailUnico(dto.getEmail());

        // Normalización de valores
        String telefonoLimpio = dto.getTelefono() != null ? dto.getTelefono().replaceAll("\\D", "") : "";
        String codigoLimpio = dto.getCodigo() != null ? dto.getCodigo().trim().toUpperCase() : "";

        if (doctorRepository.existsByTelefono(telefonoLimpio)) {
            throw new ConflictException("Ya existe un doctor con el teléfono '" + dto.getTelefono() + "'");
        }
        if (doctorRepository.existsByCodigo(codigoLimpio)) {
            throw new ConflictException("Ya existe un doctor con el código '" + dto.getCodigo() + "'");
        }

        Especialidad especialidad = especialidadRepository.findById(dto.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        Doctor doctor = new Doctor();
        doctor.setNombre(dto.getNombre());
        doctor.setApellido(dto.getApellido());
        doctor.setEmail(dto.getEmail());
        doctor.setTelefono(telefonoLimpio);
        doctor.setCodigo(codigoLimpio);
        doctor.setEspecialidad(especialidad);
        doctor = doctorRepository.save(doctor);

        String passwordTemporal = generarContraseniaAleatoria();

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(passwordTemporal));
        usuario.setEstado(true);
        usuario.setDebeCambiarContrasenia(true);
        usuario = usuarioRepository.save(usuario);

        doctor.setUsuario(usuario);
        doctorRepository.save(doctor);

        asignarRole(usuario, ROLE_DOCTOR);

        return generarRespuestaConToken(usuario, doctor.getNombre() + " " + doctor.getApellido(),
                "DOCTOR", ROLE_DOCTOR, true, passwordTemporal);
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarPersonal(RegistroPersonalDTO dto) {
        validarEmailUnico(dto.getEmail());

        String passwordTemporal = generarContraseniaAleatoria();

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(passwordTemporal));
        usuario.setEstado(true);
        usuario.setDebeCambiarContrasenia(true);
        usuario = usuarioRepository.save(usuario);

        asignarRole(usuario, ROLE_PERSONAL);

        return generarRespuestaConToken(usuario, usuario.getEmail(), "PERSONAL", ROLE_PERSONAL, true, passwordTemporal);
    }

    @Override
    @Transactional
    public LoginResponseDTO cambiarContrasenia(String nuevaContrasenia) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(authenticatedUser.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (nuevaContrasenia == null || nuevaContrasenia.length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }

        usuario.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        usuario.setDebeCambiarContrasenia(false);
        usuario = usuarioRepository.save(usuario);

        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                usuario.getEmail(),
                authenticatedUser.tipo(),
                authenticatedUser.pacienteId(),
                authenticatedUser.doctorId(),
                authenticatedUser.rol(),
                false
        );
        String token = jwtService.generarToken(principal);

        return new LoginResponseDTO(
                token,
                usuario.getEmail(),
                principal.getNombre(),
                principal.getTipo(),
                principal.getRol(),
                false,
                null
        );
    }

    @Override
    @Transactional
    public LoginResponseDTO resetearContrasenia(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado para el usuario"));

        if (ROLE_PACIENTE.equals(usuarioRole.getRole().getNombre())) {
            throw new BadRequestException("Solo se puede resetear la contraseña de usuarios empleados o médicos");
        }

        String passwordTemporal = generarContraseniaAleatoria();
        usuario.setContrasenia(passwordEncoder.encode(passwordTemporal));
        usuario.setDebeCambiarContrasenia(true);
        usuario = usuarioRepository.save(usuario);

        String rol = usuarioRole.getRole().getNombre();
        String tipo = ROLE_DOCTOR.equals(rol) ? "DOCTOR" : (ROLE_ADMIN.equals(rol) ? "ADMIN" : "PERSONAL");
        String nombre = usuario.getEmail();

        if (ROLE_DOCTOR.equals(rol)) {
            Doctor doctor = doctorRepository.findByUsuario_Id(usuario.getId()).orElse(null);
            if (doctor != null) {
                nombre = doctor.getNombre() + " " + doctor.getApellido();
            }
        }

        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                nombre,
                tipo,
                obtenerPacienteId(usuario),
                obtenerDoctorId(usuario),
                rol,
                true
        );
        String token = jwtService.generarToken(principal);

        return new LoginResponseDTO(token, usuario.getEmail(), nombre, tipo, rol, true, passwordTemporal);
    }

    @Override
    @Transactional
    public void cambiarEstadoUsuario(Integer usuarioId, boolean nuevoEstado) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        if (authenticatedUser.id() != null && authenticatedUser.id().equals(usuarioId)) {
            throw new BadRequestException("No puedes cambiar tu propio estado");
        }

        Usuario usuarioObjetivo = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado para el usuario"));

        if (ROLE_ADMIN.equals(usuarioRole.getRole().getNombre())) {
            throw new BadRequestException("No se puede cambiar el estado de un usuario con rol ADMIN");
        }

        usuarioObjetivo.setEstado(nuevoEstado);
        usuarioRepository.save(usuarioObjetivo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuario.getId())
                    .orElse(null);
            String rol = usuarioRole != null ? usuarioRole.getRole().getNombre() : "SIN_ROL";

            String nombre = usuario.getEmail();
            String tipo = rol;

            Optional<Doctor> doctorOpt = doctorRepository.findByUsuario_Id(usuario.getId());
            if (doctorOpt.isPresent()) {
                nombre = doctorOpt.get().getNombre() + " " + doctorOpt.get().getApellido();
                tipo = "DOCTOR";
            } else {
                Optional<Paciente> pacienteOpt = pacienteRepository.findByUsuario_Id(usuario.getId());
                if (pacienteOpt.isPresent()) {
                    nombre = pacienteOpt.get().getNombre() + " " + pacienteOpt.get().getApellido();
                    tipo = "PACIENTE";
                }
            }

            resultado.add(new UsuarioDTO(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.isEstado(),
                    rol,
                    nombre,
                    tipo
            ));
        }

        return resultado;
    }

    @Override
    @Transactional
    public LoginResponseDTO registrarAdmin(RegistroAdminDTO dto) {
        validarEmailUnico(dto.getEmail());

        String passwordTemporal = generarContraseniaAleatoria();

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenia(passwordEncoder.encode(passwordTemporal));
        usuario.setEstado(true);
        usuario.setDebeCambiarContrasenia(true);
        usuario = usuarioRepository.save(usuario);

        asignarRole(usuario, ROLE_ADMIN);

        return generarRespuestaConToken(usuario, usuario.getEmail(), "ADMIN", ROLE_ADMIN, true, passwordTemporal);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void validarCredencialesNuevas(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }
        if (password == null || password.length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }
        validarEmailUnico(email);
    }

    private void validarEmailUnico(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El email es obligatorio");
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

    private LoginResponseDTO generarRespuestaConToken(Usuario usuario, String nombre, String tipo, String rol,
                                                      boolean debeCambiar, String contraseniaTemporal) {
        UsuarioPrincipal principal = new UsuarioPrincipal(
                usuario,
                nombre,
                tipo,
                obtenerPacienteId(usuario),
                obtenerDoctorId(usuario),
                rol,
                debeCambiar
        );
        String token = jwtService.generarToken(principal);
        return new LoginResponseDTO(token, usuario.getEmail(), nombre, tipo, rol, debeCambiar, contraseniaTemporal);
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

    private String generarContraseniaAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
}