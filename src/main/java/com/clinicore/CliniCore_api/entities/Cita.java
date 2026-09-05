package com.clinicore.CliniCore_api.entities;

import com.clinicore.CliniCore_api.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "citas", schema = "public", catalog = "CliniCore_db")
public class Cita implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 255)

    private String motivo;
    @Column(nullable = false, length = 80)

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")

    private Paciente paciente;
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    //agregamos la relacion con doctor
    //para que qude guardado cuano se agende la cita, o si se cancela
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

}