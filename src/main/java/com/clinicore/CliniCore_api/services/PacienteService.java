package com.clinicore.CliniCore_api.services;
import com.clinicore.CliniCore_api.dto.PacienteDTO;
import com.clinicore.CliniCore_api.entities.Paciente;
import com.clinicore.CliniCore_api.entities.Usuario;

import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.exceptions.ResourceNotFoundException;
import com.clinicore.CliniCore_api.interfaces.IPacienteService;
import com.clinicore.CliniCore_api.mappers.PacienteMapper;
import com.clinicore.CliniCore_api.repository.PacienteRepository;
import com.clinicore.CliniCore_api.repository.UsuarioRepositiry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor

public class PacienteService implements IPacienteService {
    private final PacienteRepository repository;
    private final PacienteMapper mapper;
    private final UsuarioRepositiry usuarioRepositiry;
    @Override
    @Transactional(readOnly = true)
    public List<PacienteDTO>findAll(){
        //Obtiene todos los pacientes registrados
        return mapper.toDtoList(repository.findAll());
    }
    @Override
    @Transactional(readOnly = true)
    public PacienteDTO findById(Integer id) {
        // Busca paciente por ID primario y lo devuelve como DTO
        return mapper.toDTO(buscar(id));
    }
    @Override
    @Transactional(readOnly = true)
    public PacienteDTO findByCodigoExpediente(String codigoExpediente) {
        // Busca paciente por número de expediente único
        Paciente paciente = repository.findByCodigoExpediente(codigoExpediente)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un expediente con el número: " + codigoExpediente));
        return mapper.toDTO(paciente);
    }
    @Override
    @Transactional
    public PacienteDTO update(PacienteDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("El ID del paciente es obligatorio para actualizar.");
        }

        // 1. Validaciones previas de campos obligatorios
        validarCamposObligatorios(dto);

        // 2. Comprueba que el paciente exista
        Paciente existente = buscar(dto.getId());

        // 3. Valida que el DUI no esté repetido
        validarDuiUnico(dto);

        // 4. Mapea el DTO a la Entidad Paciente
        Paciente paciente = mapper.toEntity(dto);

        // 5. Vincula de forma obligatoria el Usuario de la base de datos
        Usuario usuario = buscarUsuario(dto.getUsuarioId());
        paciente.setUsuario(usuario);

        // 6. Conserva datos originales que no cambian en la actualización
        paciente.setCodigoExpediente(existente.getCodigoExpediente());
        paciente.setFechaRegistro(existente.getFechaRegistro());
        paciente.setArchivado(existente.isArchivado());

        // 7. Guarda en BD y devuelve el DTO mapeado
        return mapper.toDTO(repository.save(paciente));
    }

    @Override
    @Transactional
    public void cambiarEstadoArchivado(Integer id, boolean archivado) {
        // 1. Busca el paciente en la base de datos
        Paciente paciente = buscar(id);
        // 2. Cambia únicamente su estado de archivado
        paciente.setArchivado(archivado);
        // 3. Guarda el cambio en la base de datos
        repository.save(paciente);
    }

    // ----------------------------------------------------------------------------------------------------------------


    // Busca un paciente por su ID primario; lanza excepción si no existe
    private Paciente buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con ID: " + id));
    }

    // Comprueba que un valor no sea nulo ni vacío en caso de texto
    private void exigir(Object valor, String campo) {
        if (valor == null || (valor instanceof String && ((String) valor).isBlank())) {
            throw new BadRequestException("El campo '" + campo + "' es obligatorio.");
        }
    }

    // Valida todos los campos no opcionales del paciente
    private void validarCamposObligatorios(PacienteDTO dto) {
        exigir(dto.getNombre(), "nombre");
        exigir(dto.getApellido(), "apellido");
        exigir(dto.getDui(), "dui");
        exigir(dto.getFechaNacimiento(), "fechaNacimiento");
        exigir(dto.getGenero(), "genero");
        exigir(dto.getDireccion(), "direccion");
        exigir(dto.getTelefono(), "telefono");
        exigir(dto.getUsuarioId(), "usuarioId");
    }

    // Valida si el DUI ya está asignado a otro paciente registrado
    private void validarDuiUnico(PacienteDTO dto) {
        if (repository.existsByDuiAndIdNot(dto.getDui(), dto.getId() != null ? dto.getId() : -1)) {
            throw new BadRequestException("El DUI '" + dto.getDui() + "' ya está registrado.");
        }
    }

    // Busca el usuario y lanza error si no lo encuentra en la base de datos
    private Usuario buscarUsuario(Integer usuarioId) {
        return usuarioRepositiry.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + usuarioId + " no existe."));
    }



}
