package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.DoctorDTO;
import com.clinicore.CliniCore_api.entities.Doctor;
import com.clinicore.CliniCore_api.entities.Especialidad;
import com.clinicore.CliniCore_api.entities.Usuario;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IDoctorService;
import com.clinicore.CliniCore_api.mappers.DoctorMapper;
import com.clinicore.CliniCore_api.repository.DoctorRepository;
import com.clinicore.CliniCore_api.repository.EspecialidadRepository;
import com.clinicore.CliniCore_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService implements IDoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper mapper;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> findAll() {
        return mapper.toDtoList(doctorRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO findById(Integer id) {
        //Buscar() lanza ResourceNotFoundException si no existe
        return mapper.toDTO(buscar(id));
    }

    @Override
    @Transactional
    public DoctorDTO saveOrUpdate(DoctorDTO dto) {
        //Revisamos los campos obligatorios (nombre, apellido, codigo)
        validarCamposObligatorios(dto);

        //Si viene un id, es actualizacion - validamos que el doctor exista
        validarExisteEnActualizacion(dto);

        //Revisamos que el codigo no se repita
        validarCodigoUnico(dto);

        //Convertimos el DTO a entidad - usuario y especialidad quedan nulos
        Doctor doctor = mapper.toEntity(dto);

        //Montamos las relaciones (si vienen sus ids en el DTO)
        doctor.setEspecialidad(buscarEspecialidad(dto.getEspecialidadId()));
        doctor.setUsuario(buscarUsuario(dto.getUsuarioId()));

        //Guardamos y devolvemos el resultado como DTO
        return mapper.toDTO(doctorRepository.save(doctor));
    }

    @Override
    public void delete(Integer id) {
        //buscar() valida que exista antes de borrar
        doctorRepository.delete(buscar(id));
    }

    //------------------------------------------

    //Busca un doctor por id y lanza excepción si no lo encuentra
    private Doctor buscar(Integer id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el doctor con ID: " + id));
    }

    //Valida que un campo de texto no esté ni vacío ni en blanco
    private void exigir(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new BadRequestException("El campo " + campo + " es obligatorio");
        }
    }

    //Valida los campos obligatorios del doctor
    private void validarCamposObligatorios(DoctorDTO dto) {
        exigir(dto.getNombre(), "nombre");
        exigir(dto.getApellido(), "apellido");
        exigir(dto.getCodigo(), "codigo");
    }

    //Si el dto trae id, es una actualizacion: validamos que el doctor exista
    private void validarExisteEnActualizacion(DoctorDTO dto) {
        if (dto.getId() != null) {
            buscar(dto.getId());
        }
    }

    //Valida que el codigo sea único
    private void validarCodigoUnico(DoctorDTO dto) {
        if (doctorRepository.existsByCodigoAndIdNot(dto.getCodigo(), dto.getId())) {
            throw new BadRequestException("Ya existe otro doctor con el código " + dto.getCodigo());
        }
    }

    //Busca la especialidad por id; devuelve null si no viene el id
    private Especialidad buscarEspecialidad(Integer especialidadId) {
        if (especialidadId == null) {
            return null;
        }
        return especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la especialidad con ID: " + especialidadId));
    }

    //Busca el usuario por id; devuelve null si no viene el id
    private Usuario buscarUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con ID: " + usuarioId));
    }
}
