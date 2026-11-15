<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">

    <title>Restablecer contraseña</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/login/estiloInicioSesion.css">

</head>

<body>

<div class="background">

    <div class="card">

        <img
                src="${pageContext.request.contextPath}/OLlogo.png"
                class="logo"
                alt="OceanLink Telecom"
        >

        <h2>
            Restablecer contraseña
        </h2>

        <p>
            Ingresa tu nueva contraseña.
        </p>

        <form
                method="POST"
                action="${pageContext.request.contextPath}/LoginServlet?action=cambiarPassword"
        >

            <input
                    type="hidden"
                    name="token"
                    value="${token}"
            >

            <label>
                Nueva contraseña
            </label>

            <input
                    type="password"
                    name="password"
                    placeholder="Nueva contraseña"
                    required
            >

            <label>
                Confirmar contraseña
            </label>

            <input
                    type="password"
                    name="confirmar"
                    placeholder="Confirmar contraseña"
                    required
            >

            <div class="error">
                ${error}
            </div>

            <button type="submit">

                Cambiar contraseña

            </button>

        </form>

        <a href="${pageContext.request.contextPath}/login/loginIndex.jsp">

            Volver al inicio de sesión

        </a>

    </div>

</div>

</body>

</html>