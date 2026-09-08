package com.clinicore.CliniCore_api.services;

import com.clinicore.CliniCore_api.dto.CitaDTO;
import com.clinicore.CliniCore_api.enums.EstadoCita;
import com.clinicore.CliniCore_api.exceptions.BadRequestException;
import com.clinicore.CliniCore_api.interfaces.ICitaService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteCitasService {
    //Inyeccion de la interfaz del servivio de citas
    private final ICitaService citaService;
    //Constantes utilizadas para el diseno y formatyo de reporte PDF
    private  static final float CM = 28.3465f;
    private  static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final BaseColor COLOR_PRIMARIO = new BaseColor(37, 99, 235); // Azul  #2563EB
    private static final BaseColor COLOR_GRIS_CLARO = new BaseColor(243, 244, 246); // Gris claro #F3F4F6
    public byte[] generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Seleccione un rango de fechas válido");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha final");
        }

        // Obtener todas las citas del sistema y filtrarlas por el rango de fechas
        List<CitaDTO> todasLasCitas = citaService.findAll();
        List<CitaDTO> citas = todasLasCitas.stream()
                .filter(c -> c.getFecha() != null &&
                        !c.getFecha().isBefore(fechaInicio) &&
                        !c.getFecha().isAfter(fechaFin))
                .toList();

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            // Documento de tamaño CARTA con márgenes de 2.5 cm en los 4 lados
            Document document = new Document(PageSize.LETTER, 2.5f * CM, 2.5f * CM, 2.5f * CM, 2.5f * CM);
            PdfWriter writer = PdfWriter.getInstance(document, salida);

            // Registro del evento de pie de página (FooterEvent)
            String fechaGeneracion = LocalDate.now().format(FORMATO_FECHA);
            writer.setPageEvent(new FooterEvent(fechaGeneracion));

            document.open();
            agregarEncabezado(document, fechaInicio, fechaFin);
            agregarDetalle(document, citas);
            agregarResumen(document, citas);

            document.close();
            return salida.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el PDF de citas", e);
        }
    }
    private void agregarEncabezado(Document doc, LocalDate fechaInicio, LocalDate fechaFin)
            throws DocumentException {

        // Tabla de 2 columnas: logo (30%) | texto (70%)
        PdfPTable header = new PdfPTable(new float[]{3, 7});
        header.setWidthPercentage(100);
        header.setSpacingAfter(1 * CM);

        // --- Celda izquierda: Logo ---

        PdfPCell celdaLogo = new PdfPCell();
        celdaLogo.setBorder(Rectangle.NO_BORDER);
        celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaLogo.setPadding(4f);

        try {
            java.net.URL logoUrl = getClass().getResource("/static/logo.png");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(70, 70);
                celdaLogo.addElement(logo);
            }
        } catch (Exception ignored) {
            // Si no existe el logo simplemente se omite
        }
        header.addCell(celdaLogo);

        // --- Celda derecha: textos ---
        PdfPCell celdaTextos = new PdfPCell();
        celdaTextos.setBorder(Rectangle.NO_BORDER);
        celdaTextos.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTextos.setPaddingLeft(8f);

        Font fuenteEmpresa = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, COLOR_PRIMARIO);
        Font fuenteTitulo  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Font fuenteSubtulo = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL,
                new BaseColor(107, 114, 128)); // gris #6B7280

        Paragraph empresa = new Paragraph("CliniCore - Sistema Médico", fuenteEmpresa);
        empresa.setSpacingAfter(2f);

        Paragraph titulo = new Paragraph("Reporte de Citas Médicas", fuenteTitulo);
        titulo.setSpacingAfter(2f);

        String rango = "Período: " + fechaInicio.format(FORMATO_FECHA) + "  →  " + fechaFin.format(FORMATO_FECHA);
        Paragraph subtitulo = new Paragraph(rango, fuenteSubtulo);

        celdaTextos.addElement(empresa);
        celdaTextos.addElement(titulo);
        celdaTextos.addElement(subtitulo);
        header.addCell(celdaTextos);

        doc.add(header);

        // Línea separadora
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        linea.setSpacingAfter(0.5f * CM);
        PdfPCell separador = new PdfPCell();
        separador.setBorder(Rectangle.BOTTOM);
        separador.setBorderColor(COLOR_PRIMARIO);
        separador.setBorderWidth(1.5f);
        separador.setFixedHeight(1f);
        linea.addCell(separador);
        doc.add(linea);
    }
    private void agregarDetalle(Document doc, List<CitaDTO> citas) throws DocumentException {

        // Título de sección
        Font fuenteSeccion = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, COLOR_PRIMARIO);
        Paragraph tituloSeccion = new Paragraph("Detalle de Citas por Estado", fuenteSeccion);
        tituloSeccion.setSpacingAfter(0.4f * CM);
        doc.add(tituloSeccion);

        // Recorremos cada estado del enum en orden
        for (EstadoCita estado : EstadoCita.values()) {

            // Filtramos las citas que corresponden a este estado
            List<CitaDTO> grupo = citas.stream()
                    .filter(c -> c.getEstado() == estado)
                    .toList();

            // Si no hay citas para este estado, lo saltamos
            if (grupo.isEmpty()) continue;

            // Subtítulo del grupo (nombre del estado)
            Font fuenteGrupo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            Paragraph tituloGrupo = new Paragraph(
                    estado.name().charAt(0) + estado.name().substring(1).toLowerCase(),
                    fuenteGrupo);
            tituloGrupo.setSpacingBefore(0.4f * CM);
            tituloGrupo.setSpacingAfter(0.2f * CM);
            doc.add(tituloGrupo);

            // Tabla con las citas de este grupo
            doc.add(construirTablaCitas(grupo));

            // Franja separadora negra entre tablas
            PdfPTable separadorNegro = new PdfPTable(1);
            separadorNegro.setWidthPercentage(100);
            separadorNegro.setSpacingBefore(0.2f * CM);
            separadorNegro.setSpacingAfter(0.4f * CM);

            PdfPCell celdaFranja = new PdfPCell();
            celdaFranja.setBackgroundColor(BaseColor.BLACK);
            celdaFranja.setFixedHeight(2f);
            celdaFranja.setBorder(Rectangle.NO_BORDER);

            separadorNegro.addCell(celdaFranja);
            doc.add(separadorNegro);
        }
    }
    private void agregarResumen(Document doc, List<CitaDTO> citas) throws DocumentException {

        // Espacio antes del resumen
        doc.add(new Paragraph(" "));

        // Título de sección
        Font fuenteSeccion = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, COLOR_PRIMARIO);
        Paragraph tituloResumen = new Paragraph("Resumen General", fuenteSeccion);
        tituloResumen.setSpacingAfter(0.4f * CM);
        doc.add(tituloResumen);

        // Tabla resumen: 2 columnas → Estado | Cantidad
        PdfPTable tabla = new PdfPTable(new float[]{6, 2});
        tabla.setWidthPercentage(60); // Ocupa solo el 60 % del ancho (más compacto)
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Fila de encabezado
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell hEstado    = new PdfPCell(new Phrase("Estado", fuenteHeader));
        PdfPCell hCantidad  = new PdfPCell(new Phrase("Cantidad", fuenteHeader));

        for (PdfPCell h : new PdfPCell[]{hEstado, hCantidad}) {
            h.setBackgroundColor(COLOR_PRIMARIO);
            h.setPadding(6f);
            h.setBorder(Rectangle.NO_BORDER);
            h.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(h);
        }

        // Filas por estado
        Font fuenteFila = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        boolean filaPar = false;

        for (EstadoCita estado : EstadoCita.values()) {
            long cantidad = citas.stream()
                    .filter(c -> c.getEstado() == estado)
                    .count();

            if (cantidad == 0) continue; // omitimos estados sin citas

            BaseColor fondo = filaPar ? COLOR_GRIS_CLARO : BaseColor.WHITE;
            filaPar = !filaPar;

            PdfPCell cEstado    = new PdfPCell(new Phrase(estado.name(), fuenteFila));
            PdfPCell cCantidad  = new PdfPCell(new Phrase(String.valueOf(cantidad), fuenteFila));

            for (PdfPCell c : new PdfPCell[]{cEstado, cCantidad}) {
                c.setBackgroundColor(fondo);
                c.setPadding(5f);
                c.setBorder(Rectangle.NO_BORDER);
            }
            cCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);

            tabla.addCell(cEstado);
            tabla.addCell(cCantidad);
        }

        // Fila de total general
        Font fuenteTotal = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell cTotalLabel    = new PdfPCell(new Phrase("TOTAL GENERAL", fuenteTotal));
        PdfPCell cTotalValor    = new PdfPCell(new Phrase(String.valueOf(citas.size()), fuenteTotal));

        for (PdfPCell c : new PdfPCell[]{cTotalLabel, cTotalValor}) {
            c.setBackgroundColor(new BaseColor(30, 64, 175)); // azul más oscuro #1E40AF
            c.setPadding(6f);
            c.setBorder(Rectangle.NO_BORDER);
        }
        cTotalValor.setHorizontalAlignment(Element.ALIGN_CENTER);

        tabla.addCell(cTotalLabel);
        tabla.addCell(cTotalValor);

        doc.add(tabla);
    }
    private PdfPTable construirTablaCitas(List<CitaDTO> citas) throws DocumentException {

        // 5 columnas: Fecha | Hora | Paciente | Doctor | Estado (se quitó la columna ID)
        PdfPTable tabla = new PdfPTable(new float[]{2, 2, 3.5f, 3.5f, 2});
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(0.5f * CM);

        // Encabezados de columna
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Fecha", "Hora", "Paciente", "Doctor", "Estado"};

        for (String h : headers) {
            PdfPCell celda = new PdfPCell(new Phrase(h, fuenteHeader));
            celda.setBackgroundColor(COLOR_PRIMARIO);
            celda.setPadding(5f);
            celda.setBorder(Rectangle.BOX);
            celda.setBorderColor(new BaseColor(209, 213, 219)); // Borde gris claro #D1D5DB
            celda.setBorderWidth(0.5f);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }

        // Filas de datos
        boolean filaPar = false;

        for (CitaDTO cita : citas) {
            BaseColor fondo = filaPar ? COLOR_GRIS_CLARO : BaseColor.WHITE;
            filaPar = !filaPar;

            tabla.addCell(celdaTexto(cita.getFecha().format(FORMATO_FECHA), fondo, Element.ALIGN_CENTER));
            tabla.addCell(celdaTexto(cita.getHoraInicio().toString(), fondo, Element.ALIGN_CENTER));
            tabla.addCell(celdaTexto(cita.getPacienteNombre(), fondo, Element.ALIGN_CENTER));
            tabla.addCell(celdaTexto(cita.getDoctorNombre(), fondo, Element.ALIGN_CENTER));
            tabla.addCell(celdaEstado(cita.getEstado(), fondo));
        }

        return tabla;
    }
    private PdfPCell celdaTexto(String texto, BaseColor fondo, int alineacion) {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "-", fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(4f);
        celda.setBorder(Rectangle.BOX);
        celda.setBorderColor(new BaseColor(209, 213, 219)); // Borde gris claro #D1D5DB entre celdas/columnas
        celda.setBorderWidth(0.5f);
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return celda;
    }

    private PdfPCell celdaEstado(EstadoCita estado, BaseColor fondoFila) {
        // Color del texto según el estado
        BaseColor colorTexto;
        switch (estado) {
            case PENDIENTE   -> colorTexto = new BaseColor(161, 98,   7);   // ámbar  #A16207
            case ATENDIDA    -> colorTexto = new BaseColor(21,  128,  61);  // verde  #15803D
            case EN_ESPERA   -> colorTexto = new BaseColor(29,  78,  216);  // azul   #1D4ED8
            case CANCELADA   -> colorTexto = new BaseColor(185,  28,  28);  // rojo   #B91C1C
            case REASIGNADA  -> colorTexto = new BaseColor(109,  40, 217);  // violeta #6D28D9
            default          -> colorTexto = BaseColor.BLACK;
        }

        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, colorTexto);
        PdfPCell celda = new PdfPCell(new Phrase(estado.name(), fuente));
        celda.setBackgroundColor(fondoFila);
        celda.setPadding(4f);
        celda.setBorder(Rectangle.BOX);
        celda.setBorderColor(new BaseColor(209, 213, 219)); // Borde gris claro #D1D5DB
        celda.setBorderWidth(0.5f);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return celda;
    }
    private static class FooterEvent extends PdfPageEventHelper {

        private final String fechaGeneracion;

        FooterEvent(String fechaGeneracion) {
            this.fechaGeneracion = fechaGeneracion;
        }


        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();

            Font fuentePie = new Font(Font.FontFamily.HELVETICA, 8,
                    Font.NORMAL, new BaseColor(107, 114, 128)); // gris #6B7280

            // Texto izquierdo: fecha de generación
            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Phrase("Generado el: " + fechaGeneracion, fuentePie),
                    document.left(),
                    document.bottom() - 10,
                    0
            );

            // Texto derecho: número de página
            String textoPagina = "Página " + writer.getPageNumber();
            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    new Phrase(textoPagina, fuentePie),
                    document.right(),
                    document.bottom() - 10,
                    0
            );
        }
    }


}
