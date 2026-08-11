package com.clinicore.CliniCore_api.entities;

import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "disponibilidades", schema = "public", catalog = "CliniCore_db")
public class Disponibilidad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(nullable = false, name = "hora_inicio")
    private LocalDateTime horaInicio;
    @Column(nullable = false, name = "hora_fin")
    private LocalDateTime horaFin;
    @Column(nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private EstadoDisponibilidad estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;
}