package com.clinicore.CliniCore_api.entities;

import com.clinicore.CliniCore_api.enums.EstadoExpediente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pacientes", schema = "public", catalog = "CliniCore_db")
public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "codigo_expediente")
    private Integer codigoExpediente;
    @Column(nullable = false, name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(nullable = false, name = "fecha_registro")
    private LocalDate fechaRegistro;
    @Column(nullable = false, length = 60)
    private String nombre;
    @Column(nullable = false, length = 60)
    private String apellido;
    @Column(nullable = false, length = 80)
    private String dui;
    @Column(nullable = false, length = 20)
    private String genero;
    @Column(nullable = false, length = 255)
    private String direccion;
    @Column(nullable = false, length = 9)
    private String telefono;
    @Column(name = "alergia_intolerancia", length = 255)
    private String alergiaIntolerancian;
    @Column(nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private EstadoExpediente estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}