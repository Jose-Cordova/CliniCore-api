package com.clinicore.CliniCore_api.entities;

import com.clinicore.CliniCore_api.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
        name = "horarios_base", schema = "public", catalog = "CliniCore_db",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_horario_doctor_dia", columnNames = {"doctor_id", "dia_semana"}
        )
)
public class HorarioBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "dia_semana")
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
    @Column(nullable = false, name = "hora_inicio")
    private LocalTime horaInicio;
    @Column(nullable = false, name = "hora_fin")
    private LocalTime horaFin;
    @Column(name = "hora_almuerzo_inicio")
    private LocalTime horaAlmuerzoInicio;
    @Column(name = "hora_almuerzo_fin")
    private LocalTime horaAlmuerzoFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
}
