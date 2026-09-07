package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.PerfilDTO;
import com.clinicore.CliniCore_api.entities.*;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ConflictException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IUsuarioService;
import com.clinicore.CliniCore_api.repository.*;
import com.clinicore.CliniCore_api.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DoctorRepository doctorRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfil() {
        AuthenticatedUser authUser = getUsuarioAutenticado();
        Usuario usuario = usuarioRepository.findByEmail(authUser.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        String rol = usuarioRole.getRole().getNombre();

        PerfilDTO perfil = new PerfilDTO();
        perfil.setId(usuario.getId());
        perfil.setEmail(usuario.getEmail());
        perfil.setTipo(rol);

        if ("DOCTOR".equals(rol)) {
            Doctor doctor = doctorRepository.findByUsuario_Id(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
            perfil.setNombre(doctor.getNombre());
            perfil.setApellido(doctor.getApellido());
            perfil.setTelefono(doctor.getTelefono());
            perfil.setCodigo(doctor.getCodigo());
            perfil.setEspecialidadId(doctor.getEspecialidad() != null ? doctor.getEspecialidad().getId() : null);
        }

        return perfil;
    }

    @Override
    @Transactional
    public PerfilDTO actualizarPerfil(PerfilDTO dto) {
        AuthenticatedUser authUser = getUsuarioAutenticado();
        Usuario usuario = usuarioRepository.findByEmail(authUser.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioRole usuarioRole = usuarioRoleRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        String rol = usuarioRole.getRole().getNombre();

        if ("DOCTOR".equals(rol)) {
            actualizarDoctor(usuario, dto);
        } else if ("ADMIN".equals(rol) || "PERSONAL".equals(rol)) {
            // No hay datos editables para admin/personal
            // Solo email que no se modifica
        } else {
            throw new BadRequestException("No se permite actualizar perfil para este rol");
        }

        return obtenerPerfil();
    }

    private void actualizarDoctor(Usuario usuario, PerfilDTO dto) {
        Doctor doctor = doctorRepository.findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        // Validar y normalizar teléfono
        String telefonoLimpio = dto.getTelefono() != null ? dto.getTelefono().replaceAll("\\D", "") : "";
        if (telefonoLimpio.length() != 8) {
            throw new BadRequestException("El teléfono debe tener 8 dígitos");
        }
        if (doctorRepository.existsByTelefonoAndIdNot(telefonoLimpio, doctor.getId())) {
            throw new ConflictException("Ya existe un doctor con el teléfono '" + telefonoLimpio + "'");
        }

        // Validar y normalizar código
        String codigoLimpio = dto.getCodigo() != null ? dto.getCodigo().trim().toUpperCase() : "";
        if (codigoLimpio.length() < 3) {
            throw new BadRequestException("El código colegiado es obligatorio");
        }
        if (doctorRepository.existsByCodigoAndIdNot(codigoLimpio, doctor.getId())) {
            throw new ConflictException("Ya existe un doctor con el código '" + codigoLimpio + "'");
        }

        Especialidad especialidad = especialidadRepository.findById(dto.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        doctor.setNombre(dto.getNombre());
        doctor.setApellido(dto.getApellido());
        doctor.setTelefono(telefonoLimpio);
        doctor.setCodigo(codigoLimpio);
        doctor.setEspecialidad(especialidad);
        doctorRepository.save(doctor);
    }

    private AuthenticatedUser getUsuarioAutenticado() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}