
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Recuperar contraseña - Error</title>

    <link rel="stylesheet"
          type="text/css"
          href="<%= request.getContextPath() %>/login/estiloInicioSesion.css">

</head>

<body>

<div class="background">

    <div class="card">

        <!-- LOGO OCEANLINK -->
        <img src="<%= request.getContextPath() %>/OLlogo.png"
             class="logo"
             alt="OceanLink Telecom">

        <!-- TITULO -->
        <h2>
            Recuperar contraseña
        </h2>

        <!-- DESCRIPCION -->
        <p>
            Ingresa el correo asociado a tu cuenta.
        </p>

        <!-- FORMULARIO PARA VOLVER A INTENTAR -->
        <form action="<%= request.getContextPath() %>/LoginServlet"
              method="POST">

            <input type="hidden" name="action" value="recuperar">

            <label for="correo">
                Correo electrónico
            </label>

            <!-- CORREO INGRESADO ANTERIORMENTE -->
            <input
                type="email"
                id="correo"
                name="correo"
                value="${param.correo}"
                placeholder="Ingrese su correo electrónico"
                required>

            <!-- MENSAJE DE ERROR -->
            <div class="error-message">

                <span class="error-icon">
                    !
                </span>

                <span>
                    El correo ingresado no se encuentra registrado.
                </span>

            </div>

            <!-- BOTON PARA REINTENTAR -->
            <button type="submit">

                Enviar enlace

            </button>

        </form>

        <!-- VOLVER AL LOGIN -->
        <a href="<%= request.getContextPath() %>/login/loginIndex.jsp">

            Volver al inicio de sesión

        </a>

    </div>

</div>

</body>
</html>
