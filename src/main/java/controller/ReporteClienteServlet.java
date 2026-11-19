
package controller;

import beans.Cliente;
import beans.Usuario;
import dao.ClienteDAO;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(
            name = "ReporteClienteServlet",
            value = "/ReporteClienteServlet")
public class ReporteClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
    //Verificacion de admin
        HttpSession sesion = request.getSession(false);

        Usuario usuario = sesion == null ? null
                : (Usuario) sesion.getAttribute("usuarioSesion");

        if (usuario == null || usuario.getRol() == null
                || usuario.getRol().getIdRol() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para descargar este reporte.");
            return;
        }

        //Obtiene clientes
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> clientes = dao.listarClientes(null, null);

        //Descarga
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"Reporte_Clientes_OCEANLINK.pdf\"");
        //Doc
        Document documento = new Document(PageSize.A4.rotate(),
                30, 30, 35, 35);

        try {
            PdfWriter.getInstance(documento,
                    response.getOutputStream());

            documento.open();

            Color azul = new Color(12, 48, 95);
            Color celeste = new Color(225, 240, 250);

            Font tituloFont = new Font(Font.HELVETICA,
                    20, Font.BOLD, azul);

            Font subtituloFont = new Font(Font.HELVETICA,
                    12, Font.BOLD, Color.DARK_GRAY);

            Font normalFont = new Font(Font.HELVETICA,
                    9, Font.NORMAL, Color.BLACK);

            Font encabezadoFont = new Font(Font.HELVETICA,
                    9, Font.BOLD, Color.WHITE);


            Paragraph titulo = new Paragraph(
                    "OCEANLINK - REPORTE DE CLIENTES", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(12);
            documento.add(titulo);

            String fecha = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            Paragraph fechaReporte = new Paragraph(
                    "Fecha de generación: " + fecha, normalFont);
            fechaReporte.setSpacingAfter(10);
            documento.add(fechaReporte);

            int totalServicios = 0;
            int totalSolicitudes = 0;

            for (Cliente c : clientes) {
                totalServicios += c.getCantidadServicios();
                totalSolicitudes += c.getCantidadSolicitudes();
            }

            documento.add(new Paragraph(
                    "Total de clientes: " + clientes.size(),
                    subtituloFont));

            documento.add(new Paragraph(
                    "Servicios registrados: " + totalServicios,
                    normalFont));

            Paragraph solicitudes = new Paragraph(
                    "Solicitudes registradas: " + totalSolicitudes,
                    normalFont);
            solicitudes.setSpacingAfter(18);
            documento.add(solicitudes);

            // Tabla
            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{
                    0.6f, 2.5f, 1.2f, 2.1f, 1.2f, 0.8f, 0.9f
            });

            String[] encabezados = {
                    "ID", "Razón social", "RUC", "Correo",
                    "Teléfono", "Servicios", "Solicitudes"
            };

            for (String texto : encabezados) {
                PdfPCell celda = new PdfPCell(
                        new Phrase(texto, encabezadoFont));
                celda.setBackgroundColor(azul);
                celda.setPadding(7);
                tabla.addCell(celda);
            }

            tabla.setHeaderRows(1);

            int fila = 0;

            for (Cliente c : clientes) {
                Color fondo = fila % 2 == 0
                        ? Color.WHITE : celeste;

                agregarCelda(tabla,
                        String.valueOf(c.getIdCliente()),
                        normalFont, fondo);

                agregarCelda(tabla, c.getRazonSocial(),
                        normalFont, fondo);

                agregarCelda(tabla, c.getRuc(),
                        normalFont, fondo);

                agregarCelda(tabla, c.getCorreo(),
                        normalFont, fondo);

                agregarCelda(tabla, c.getTelefono(),
                        normalFont, fondo);

                agregarCelda(tabla,
                        String.valueOf(c.getCantidadServicios()),
                        normalFont, fondo);

                agregarCelda(tabla,
                        String.valueOf(c.getCantidadSolicitudes()),
                        normalFont, fondo);

                fila++;
            }

            documento.add(tabla);

            Paragraph pie = new Paragraph(
                    "Documento generado por el sistema OCEANLINK.",
                    normalFont);
            pie.setSpacingBefore(18);
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

        } catch (DocumentException e) {
            throw new ServletException(
                    "Error al generar el reporte PDF.", e);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    private void agregarCelda(PdfPTable tabla, String valor,
                              Font fuente, Color fondo) {
        PdfPCell celda = new PdfPCell(
                new Phrase(valor == null ? "-" : valor, fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(6);
        tabla.addCell(celda);
    }
}
