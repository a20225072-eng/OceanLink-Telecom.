package controller;

import beans.Usuario;
import dao.LoginDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import service.CorreoService;
import jakarta.mail.MessagingException;
import java.util.UUID;
import java.io.IOException;

@WebServlet(
        name = "LoginServlet",
        value = "/LoginServlet"
)
public class LoginServlet extends HttpServlet {

    private static final int DURACION_SESION = 30 * 60;

    static String paginaInicio(int rolId) {
        switch (rolId) {
            case 1: return "/UsuarioServlet?action=lista";
            case 2: return "/CapacityPlannerServlet?action=capacidad";
            case 3: return "/NetworkOperatorServlet?action=vistaGeneral";
            case 4: return "/MantenimientoServlet?action=listar";
            case 5: return "/SupervisorServlet?action=dashboard";
            default: return null;
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action") == null
                ? ""
                : request.getParameter("action");

        if (action.equals("restablecer")) {

            String token = request.getParameter("token");

            LoginDAO loginDAO = new LoginDAO();

            if (token != null && loginDAO.validarToken(token)) {

                request.setAttribute("token", token);

                request.getRequestDispatcher(
                        "/login/restablecerContrasenia.jsp"
                ).forward(request, response);

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login/recuperarContrasenia.jsp"
                );
            }

            return;
        }

        if (action.equals("logout")) {

            HttpSession session = request.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/login/loginIndex.jsp"
            );

            return;
        }

        HttpSession session = request.getSession(false);

        Usuario usuarioSesion = session == null
                ? null
                : (Usuario) session.getAttribute("usuarioSesion");

        if (usuarioSesion != null && usuarioSesion.getRol() != null
                && paginaInicio(usuarioSesion.getRol().getIdRol()) != null) {

            response.sendRedirect(
                    request.getContextPath()
                            + paginaInicio(usuarioSesion.getRol().getIdRol())
            );

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/login/loginIndex.jsp"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "login";
        }

        System.out.println("Accion recibida: " + action);

        LoginDAO loginDAO = new LoginDAO();

        if (action.equals("login")) {

            String correo = request.getParameter("correo");

            String password = request.getParameter("password");

            Usuario usuario = loginDAO.validarLogin(
                    correo,
                    password
            );

            if (usuario != null && usuario.getRol() != null
                    && paginaInicio(usuario.getRol().getIdRol()) != null) {

                HttpSession session = request.getSession();

                session.setAttribute("usuarioSesion", usuario);

                session.setMaxInactiveInterval(DURACION_SESION);

                response.sendRedirect(
                        request.getContextPath()
                                + paginaInicio(usuario.getRol().getIdRol())
                );

            } else {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login/loginError.jsp?correo="
                                + java.net.URLEncoder.encode(
                                correo == null ? "" : correo,
                                "UTF-8"
                        )
                );
            }

        } else if (action.equals("recuperar")) {

            String correo = request.getParameter("correo");

            System.out.println("Correo recibido: " + correo);

            boolean existe = loginDAO.existeCorreo(correo);

            System.out.println("Existe correo: " + existe);

            if (existe) {

                String token = UUID.randomUUID().toString();

                boolean guardado = loginDAO.guardarToken(correo, token);

                System.out.println("Token guardado: " + guardado);

                if (guardado) {

                    String enlace = "http://localhost:8080"
                            + request.getContextPath()
                            + "/LoginServlet?action=restablecer&token="
                            + token;

                    CorreoService correoService = new CorreoService();

                    try {

                        System.out.println("Enviando correo...");

                        correoService.enviarCorreoRecuperacion(
                                correo,
                                enlace
                        );

                        System.out.println("Correo enviado correctamente");

                        response.sendRedirect(
                                request.getContextPath()
                                        + "/login/correoEnviado.jsp"
                        );

                    } catch (MessagingException e) {

                        System.out.println("Error al enviar el correo");

                        e.printStackTrace();

                        response.sendError(
                                500,
                                "No se pudo enviar el correo"
                        );
                    }

                } else {

                    System.out.println("No se pudo guardar el token");

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/login/recuperarContraseniaError.jsp"
                    );
                }

            } else {

                System.out.println("No se encontro el correo");

                response.sendRedirect(
                        request.getContextPath()
                                + "/login/recuperarContraseniaError.jsp"
                );
            }

        } else if (action.equals("cambiarPassword")) {

            String token = request.getParameter("token");

            String password = request.getParameter("password");

            String confirmar = request.getParameter("confirmar");

            if (token == null || !loginDAO.validarToken(token)) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login/recuperarContrasenia.jsp"
                );

                return;
            }

            if (password != null
                    && !password.trim().isEmpty()
                    && password.equals(confirmar)) {

                boolean actualizado =
                        loginDAO.actualizarPassword(token, password);

                System.out.println("Contrasenia actualizada: " + actualizado);

                if (actualizado) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/login/loginIndex.jsp"
                    );

                } else {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/login/recuperarContrasenia.jsp"
                    );
                }

            } else {

                request.setAttribute("token", token);

                request.setAttribute(
                        "error",
                        "Las contraseñas no coinciden o están vacías."
                );

                request.getRequestDispatcher(
                        "/login/restablecerContrasenia.jsp"
                ).forward(request, response);
            }

        } else {

            response.sendRedirect(
                    request.getContextPath()
                            + "/login/loginIndex.jsp"
            );
        }
    }
}