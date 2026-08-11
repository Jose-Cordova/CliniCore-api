package com.clinicore.CliniCore_api.entities;

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
@Table(name = "expedientes", schema = "public", catalog = "CliniCore_db")
public class Expediente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer codigo;
    @Column(nullable = false, name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column(nullable = false, length = 20)
    private String genero;
    @Column(nullable = false, length = 255)
    private String direccion;
    @Column(nullable = false, length = 9)
    private String telefono;
    @Column(name = "alergia_intolerancia", length = 255)
    private String alergiaIntoleracian;

    @Column(length = 255)
    private String estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}