package com.clinicore.CliniCore_api.entities;

import com.clinicore.CliniCore_api.enums.EstadoDisponibilidad;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
        name = "disponibilidades", schema = "public", catalog = "CliniCore_db",
        indexes = {
                @Index(name = "idx_doctor_id", columnList = "doctor_id"),
                @Index(name = "idx_cita_id", columnList = "cita_id")
        }
)
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