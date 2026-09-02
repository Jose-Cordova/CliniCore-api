package com.clinicore.CliniCore_api.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PacienteDTO {
    // ID único del paciente en la base de datos
    private Integer id;

    // Número correlativo de expediente que se genera automáticamente
    private String codigoExpediente;

    // Fecha de nacimiento del paciente
    private LocalDate fechaNacimiento;

    // Fecha en que se registró el paciente en el sistema
    private LocalDate fechaRegistro;

    // Nombre completo del paciente
    private String nombre;

    // Apellido completo del paciente
    private String apellido;

    // Documento Único de Identidad del paciente (único y obligatorio)
    private String dui;

    // Género del paciente (Ej. Masculino, Femenino)
    private String genero;

    // Dirección residencial
    private String direccion;

    // Número de teléfono de contacto
    private String telefono;

    // Alergias o condiciones especiales (Opcional, puede ir vacío)
    private String alergiaIntolerancia;

    // Indica si el expediente está archivado
    private boolean archivado;

    // ID del usuario asociado - Obligatorio para vincular su cuenta de inicio de sesión
    private Integer usuarioId;
}
