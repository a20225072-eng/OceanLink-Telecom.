
package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class CorreoService {

    public void enviarCorreoRecuperacion(String destino, String enlace)
            throws MessagingException {

        String host = System.getenv().getOrDefault(
                "SMTP_HOST", "sandbox.smtp.mailtrap.io");

        String port = System.getenv().getOrDefault(
                "SMTP_PORT", "2525");

        String user = System.getenv("SMTP_USER"),
                pass = System.getenv("SMTP_PASSWORD");

        String remitente = System.getenv().getOrDefault(
                "SMTP_FROM", "notificaciones@operadora.example");

        if (user == null || user.trim().isEmpty() ||
                pass == null || pass.trim().isEmpty())

            throw new MessagingException(
                    "Configure SMTP_USER y SMTP_PASSWORD");

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session sesion = Session.getInstance(
                props, new Authenticator() {

                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(user, pass);
                    }
                });

        MimeMessage mensaje = new MimeMessage(sesion);

        mensaje.setFrom(new InternetAddress(remitente));

        mensaje.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destino, true));

        mensaje.setSubject(
                "Recuperar contraseña - OceanLink", "UTF-8");

        String html =
                "<html>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>OceanLink</title>" +
                        "</head>" +

                        "<body bgcolor='#f2f6fa'>" +

                        "<table width='100%' cellpadding='20'>" +
                        "<tr><td align='center'>" +

                        "<table width='550' bgcolor='white' cellpadding='20'>" +

                        // ENCABEZADO
                        "<tr>" +
                        "<td bgcolor='#123456' align='center'>" +

                        "<h1><font color='white'>OCEANLINK</font></h1>" +

                        "<p><font color='white'>TELECOMUNICACIONES</font></p>" +

                        "</td>" +
                        "</tr>" +

                        // MENSAJE
                        "<tr>" +
                        "<td>" +

                        "<h2>Restablecimiento de contraseña</h2>" +

                        "<p>Hola,</p>" +

                        "<p>Hemos recibido una solicitud para restablecer " +
                        "la contraseña de tu cuenta de OceanLink.</p>" +

                        "<p>Para continuar con el proceso, ingresa al siguiente enlace:</p>" +

                        // BOTON
                        "<table align='center' cellpadding='15'>" +
                        "<tr>" +
                        "<td bgcolor='#087da9' align='center'>" +
                        "<a href='" + enlace + "'>" +
                        "<font color='white'><b>RESTABLECER MI CONTRASEÑA</b></font>" +
                        "</a>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +

                        "<br>" +

                        "<p><b>Importante:</b> Este enlace solo puede utilizarse una vez.</p>" +

                        "<p>Si no solicitaste este cambio, puedes ignorar este correo.</p>" +

                        "<br>" +

                        "<p>Atentamente,</p>" +
                        "<p><b>Equipo de OceanLink Telecom</b></p>" +

                        "</td>" +
                        "</tr>" +

                        // PIE DEL CORREO
                        "<tr>" +
                        "<td bgcolor='#e5edf5' align='center'>" +
                        "<p>Este es un mensaje automático de OceanLink.</p>" +
                        "<p>© OceanLink Telecom</p>" +
                        "</td>" +
                        "</tr>" +

                        "</table>" +
                        "</td></tr>" +
                        "</table>" +

                        "</body>" +
                        "</html>";

        mensaje.setContent(html, "text/html; charset=UTF-8");

        Transport.send(mensaje);
    }
}
