package com.clinicore.CliniCore_api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
        name = "consultas", schema = "public", catalog = "CliniCore_db",
        indexes = {
                @Index(name = "idx_expediente_id", columnList = "expediente_id")
        }

)
public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "fecha_atencion")
    private LocalDateTime fechaAtencion;
    @Column(nullable = false, name = "tiraje_pa", length = 20)
    private String tirajePa;
    @Column(nullable = false, name = "tiraje_temperatura", precision = 12, scale = 2)
    private BigDecimal tirajeTemperatura;
    @Column(nullable = false, name = "tiraje_peso", precision = 12, scale = 2)
    private BigDecimal tirajePeso;
    @Column(nullable = false, name = "tiraje_estatura", precision = 12, scale = 2)
    private BigDecimal tirajeEstatura;
    @Column(nullable = false, name = "tiraje_sintomas", length = 255)
    private String tirajeSintomas;
    @Column(nullable = false, length = 255)
    private String diagnostico;
    @Column(nullable = false, length = 255)
    private String tratamiento;
    @Column(length = 255)
    private String nota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id")
    private Expediente expediente;
}