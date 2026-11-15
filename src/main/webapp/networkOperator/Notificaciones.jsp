
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="beans.Cliente" %>
<%@ page import="beans.Incidencia" %>
<%@ page import="beans.Mantenimiento" %>

<%
    List<Incidencia> incidencias =
            (List<Incidencia>) request.getAttribute("incidencias");

    Incidencia incidenciaSeleccionada =
            (Incidencia) request.getAttribute("incidenciaSeleccionada");

    List<Cliente> clientesAfectados =
            (List<Cliente>) request.getAttribute("clientesAfectados");

    Mantenimiento mantenimiento =
            (Mantenimiento) request.getAttribute("mantenimiento");

    String codigoSeleccionado =
            (String) request.getAttribute("codigoSeleccionado");

    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Notificaciones | OceanLink</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/networkOperator/networkOperator.css">
</head>

<body>

<jsp:include page="menu.jsp">
    <jsp:param name="activo" value="notificaciones"/>
</jsp:include>

<main class="content">

    <div class="topbar">
        <div>
            <h1>Notificaciones por correo</h1>
            <p class="small">
                Comunicacion de incidencias a los clientes
                afectados de OceanLink.
            </p>
        </div>
    </div>

    <% if (mensaje != null) { %>
        <div class="alert ok">
            <%= mensaje %>
        </div>
    <% } %>

    <% if (error != null) { %>
        <div class="alert error">
            <%= error %>
        </div>
    <% } %>

    <section class="card">

        <div class="card-header">
            <h2>Consultar incidencia</h2>
        </div>

        <p class="small">
            Seleccione una incidencia para identificar
            automaticamente las empresas afectadas.
        </p>

        <form action="<%= request.getContextPath() %>/correo"
              method="get">

            <div class="row">
                <div class="col">

                    <label for="codigoIncidencia">
                        Incidencia registrada
                    </label>

                    <select name="codigoIncidencia"
                            id="codigoIncidencia"
                            required>

                        <option value="">
                            Seleccione una incidencia
                        </option>

                        <% if (incidencias != null) {
                            for (Incidencia i : incidencias) { %>

                            <option value="<%= i.getCodigoIncidencia() %>"
                                <%= i.getCodigoIncidencia().equals(codigoSeleccionado)
                                        ? "selected" : "" %>>

                                <%= i.getCodigoIncidencia() %>
                                - <%= i.getTitulo() %>

                            </option>

                        <% }
                        } %>

                    </select>

                </div>
            </div>

            <div class="footer-action">
                <button type="submit" class="primary">
                    Ver empresas afectadas
                </button>
            </div>

        </form>

    </section>

    <% if (incidenciaSeleccionada != null) { %>

    <section class="card">

        <div class="card-header">
            <h2>Informacion de la incidencia</h2>
        </div>

        <p>
            <strong>Codigo:</strong>
            <%= incidenciaSeleccionada.getCodigoIncidencia() %>
        </p>

        <p>
            <strong>Problema:</strong>
            <%= incidenciaSeleccionada.getTitulo() %>
        </p>

        <p>
            <strong>Descripcion:</strong>
            <%= incidenciaSeleccionada.getDescripcion() %>
        </p>

        <p>
            <strong>Estado:</strong>
            <%= incidenciaSeleccionada.getEstado() %>
        </p>

    </section>

    <section class="card">

        <div class="card-header">
            <h2>Mantenimiento relacionado</h2>
        </div>

        <% if (mantenimiento != null) { %>

            <p>
                <strong>Codigo:</strong>
                <%= mantenimiento.getCodigoMantenimiento() %>
            </p>

            <p>
                <strong>Tipo:</strong>
                <%= mantenimiento.getTipo() %>
            </p>

            <p>
                <strong>Estado:</strong>
                <%= mantenimiento.getEstado() %>
            </p>

            <p>
                <strong>Fecha programada:</strong>
                <%= mantenimiento.getFechaProgramada() %>
            </p>

            <p>
                <strong>Hora de inicio:</strong>
                <%= mantenimiento.getHoraInicio() %>
            </p>

            <p>
                <strong>Duracion estimada:</strong>
                <%= mantenimiento.getDuracionEstimada() %>
            </p>

            <p class="small">
                La fecha de restablecimiento del servicio
                esta pendiente de confirmacion.
            </p>

        <% } else { %>

            <p class="small">
                No se encontro un mantenimiento correctivo
                pendiente asociado a los segmentos
                de esta incidencia.
            </p>

            <p class="small">
                Fecha estimada de solucion:
                pendiente de confirmacion.
            </p>

        <% } %>

    </section>

    <section class="card">

        <div class="card-header">
            <h2>Clientes afectados</h2>
        </div>

        <p class="small">
            Empresas con servicios activos relacionados
            con los segmentos de esta incidencia.
        </p>

        <% if (clientesAfectados != null &&
               !clientesAfectados.isEmpty()) { %>

            <table>

                <thead>
                <tr>
                    <th>Empresa</th>
                    <th>Correo electronico</th>
                    <th>Servicios afectados</th>
                </tr>
                </thead>

                <tbody>

                <% for (Cliente c : clientesAfectados) { %>

                    <tr>
                        <td>
                            <%= c.getRazonSocial() %>
                        </td>

                        <td>
                            <%= c.getCorreo() %>
                        </td>

                        <td>
                            <%= c.getCantidadServicios() %>
                        </td>
                    </tr>

                <% } %>

                </tbody>

            </table>

            <p class="small">
                Total de empresas afectadas:
                <%= clientesAfectados.size() %>
            </p>

            <p class="small">
                Revise las empresas y los datos del
                mantenimiento antes de enviar.
            </p>

            <form action="<%= request.getContextPath() %>/correo"
                  method="post">

                <input type="hidden"
                       name="codigoIncidencia"
                       value="<%= incidenciaSeleccionada.getCodigoIncidencia() %>">

                <div class="footer-action">

                    <button type="submit" class="primary">
                        Enviar notificaciones
                    </button>

                </div>

            </form>

        <% } else { %>

            <p class="small">
                No se encontraron empresas con servicios
                activos afectados por esta incidencia.
            </p>

        <% } %>

    </section>

    <% } %>

</main>
</div>

</body>
</html>
