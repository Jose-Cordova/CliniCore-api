package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.services.ReporteCitasService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    // Inyección de la dependencia del servicio de reportes de citas
    private final ReporteCitasService reporteCitasService;

    // Endpoint GET /api/reportes/citas?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
    @GetMapping("/citas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generarReporteCitas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Genera el PDF
        byte[] pdf = reporteCitasService.generarReporte(fechaInicio, fechaFin);

        // Nombre dinámico con el que se descargará el archivo PDF
        String nombreArchivo = "reporte-citas-" + fechaInicio + "-a-" + fechaFin + ".pdf";

        // Retorna la respuesta con headers de descarga de archivo PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo +
                        "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}






