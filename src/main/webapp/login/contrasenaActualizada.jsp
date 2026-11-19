
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Contraseña actualizada - OceanLink</title>

    <link rel="stylesheet"
          type="text/css"
          href="<%= request.getContextPath() %>/login/estiloInicioSesion.css">
</head>

<body class="pagina-confirmacion">

    <div class="contenedor">

        <!-- LOGO DE OCEANLINK -->
        <img src="<%= request.getContextPath() %>/OLogo.png"
             alt="OceanLink"
             class="logo">

        <!-- ICONO DE CONFIRMACION -->
        <div class="icono">
            &#10003;
        </div>

        <!-- MENSAJE -->
        <h1>¡Contraseña actualizada!</h1>

        <p>
            Tu contraseña se ha cambiado correctamente.
        </p>

        <p>
            Ahora puedes iniciar sesión en OceanLink
            utilizando tu nueva contraseña.
        </p>

        <!-- REGRESAR AL LOGIN -->
        <a href="<%= request.getContextPath() %>/login/loginIndex.jsp"
           class="boton">
            Volver al inicio de sesión
        </a>

    </div>

</body>
</html>