
package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import beans.Cliente;
import beans.Incidencia;
import beans.Mantenimiento;

import dao.IncidenciaDAO;
import dao.MantenimientoDAO;

import service.CorreoService;

import java.io.IOException;
import java.util.List;

@WebServlet("/correo")
public class CorreoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String codigo = req.getParameter("codigoIncidencia");

        try {

            if (codigo == null || codigo.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Seleccione una incidencia");
            }

            IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

            Incidencia incidencia =
                    incidenciaDAO.obtenerIncidencia(codigo);

            if (incidencia == null) {
                throw new IllegalArgumentException(
                        "La incidencia no existe");
            }

            List<Cliente> clientes =
                    incidenciaDAO.listarClientesAfectados(codigo);

            if (clientes == null || clientes.isEmpty()) {
                throw new IllegalArgumentException(
                        "No se encontraron clientes afectados");
            }

            Mantenimiento mantenimiento =
                    new MantenimientoDAO()
                            .obtenerMantenimientoDeIncidencia(codigo);

            String informacionMantenimiento =
                    obtenerInformacionMantenimiento(mantenimiento);

            CorreoService correoService = new CorreoService();

            int enviados = 0;
            int fallidos = 0;

            // Enviar un correo a cada empresa afectada
            for (Cliente cliente : clientes) {

                if (cliente.getCorreo() == null ||
                        cliente.getCorreo().trim().isEmpty()) {

                    fallidos++;
                    continue;
                }

                try {

                    correoService.enviar(
                            cliente.getCorreo(),
                            cliente.getRazonSocial(),
                            incidencia.getCodigoIncidencia(),
                            incidencia.getTitulo(),
                            incidencia.getDescripcion(),
                            informacionMantenimiento
                    );

                    enviados++;

                } catch (Exception e) {

                    fallidos++;
                    e.printStackTrace();
                }

                // Pausa para evitar envios demasiado rapidos
                try {

                    Thread.sleep(11000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }

            req.setAttribute("mensaje",
                    "Correos enviados correctamente a Mailtrap: "
                            + enviados);

            if (fallidos > 0) {

                req.setAttribute("error",
                        "No se pudieron enviar "
                                + fallidos + " correos.");
            }

        } catch (Exception e) {

            req.setAttribute("error",
                    "No se pudo enviar: " + e.getMessage());
        }

        mostrar(req, resp, codigo);
    }

    private String obtenerInformacionMantenimiento(
            Mantenimiento mantenimiento) {

        if (mantenimiento == null) {

            return "Fecha estimada de solucion "
                    + "pendiente de confirmacion.";
        }

        return "Codigo de mantenimiento: "
                + mantenimiento.getCodigoMantenimiento()
                + "\nEstado: "
                + mantenimiento.getEstado()
                + "\nFecha programada: "
                + mantenimiento.getFechaProgramada()
                + "\nHora de inicio: "
                + mantenimiento.getHoraInicio()
                + "\nDuracion estimada: "
                + mantenimiento.getDuracionEstimada()
                + "\nFecha de restablecimiento: "
                + "pendiente de confirmacion.";
    }

    private void mostrar(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        mostrar(req, resp,
                req.getParameter("codigoIncidencia"));
    }

    private void mostrar(HttpServletRequest req,
                         HttpServletResponse resp,
                         String codigo)
            throws ServletException, IOException {

        try {

            IncidenciaDAO incidenciaDAO = new IncidenciaDAO();

            req.setAttribute("incidencias",
                    incidenciaDAO.listarIncidenciasParaNotificar());

            if (codigo != null && !codigo.trim().isEmpty()) {

                Incidencia incidencia =
                        incidenciaDAO.obtenerIncidencia(codigo);

                req.setAttribute("codigoSeleccionado", codigo);

                if (incidencia != null) {

                    req.setAttribute("incidenciaSeleccionada",
                            incidencia);

                    req.setAttribute("clientesAfectados",
                            incidenciaDAO.listarClientesAfectados(codigo));

                    req.setAttribute("mantenimiento",
                            new MantenimientoDAO()
                                    .obtenerMantenimientoDeIncidencia(codigo));
                }
            }

            req.getRequestDispatcher(
                            "/networkOperator/Notificaciones.jsp")
                    .forward(req, resp);

        } catch (Exception e) {

            throw new ServletException(e);
        }
    }
}
