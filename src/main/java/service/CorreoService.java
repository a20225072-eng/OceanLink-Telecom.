
package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class CorreoService {

    public void enviar(String destino, String nombre)
            throws MessagingException {

        enviar(destino, nombre, "", "",
                "Notificacion de servicio OceanLink.",
                "Fecha de solucion pendiente de confirmacion.");
    }

    public void enviar(String destino,
                       String nombre,
                       String codigo,
                       String titulo,
                       String descripcion,
                       String informacionMantenimiento)
            throws MessagingException {

        String host = System.getenv().getOrDefault(
                "SMTP_HOST", "sandbox.smtp.mailtrap.io");

        String port = System.getenv().getOrDefault(
                "SMTP_PORT", "2525");

        String user = System.getenv("SMTP_USER");
        String pass = System.getenv("SMTP_PASSWORD");

        String remitente = System.getenv().getOrDefault(
                "SMTP_FROM", "pruebas@operadora.example");

        if (user == null || user.trim().isEmpty()
                || pass == null || pass.trim().isEmpty()) {

            throw new MessagingException(
                    "Configure SMTP_USER y SMTP_PASSWORD");
        }

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session sesion = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(user, pass);
                    }
                }
        );

        MimeMessage mensaje = new MimeMessage(sesion);

        mensaje.setFrom(new InternetAddress(remitente));

        mensaje.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destino, true)
        );

        mensaje.setSubject(
                "Aviso de incidencia " + codigo + " - OceanLink",
                "UTF-8"
        );

        String contenido =
                "Estimado cliente " + nombre + ":\n\n" +
                        "Le informamos que OceanLink ha registrado " +
                        "una incidencia relacionada con la ruta " +
                        "de su servicio de conectividad.\n\n" +

                        "INFORMACION DE LA INCIDENCIA\n" +
                        "Codigo: " + codigo + "\n" +
                        "Problema: " + titulo + "\n" +
                        "Descripcion: " + descripcion + "\n\n" +

                        "INFORMACION DEL MANTENIMIENTO\n" +
                        informacionMantenimiento + "\n\n" +

                        "Nuestro equipo tecnico se encuentra " +
                        "evaluando y atendiendo la incidencia.\n" +
                        "Se comunicaran las actualizaciones " +
                        "cuando esten disponibles.\n\n" +

                        "Agradecemos su comprension y ofrecemos " +
                        "disculpas por los inconvenientes.\n\n" +

                        "Atentamente,\n" +
                        "Equipo OceanLink Telecom";

        mensaje.setText(contenido, "UTF-8");

        Transport.send(mensaje);
    }
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

        mensaje.setText(
                "Hola,\n\n" +
                        "Para restablecer tu contraseña, ingresa al siguiente enlace:\n\n" +
                        enlace +
                        "\n\nEl enlace solo puede utilizarse una vez." +
                        "\n\nSaludos,\nOceanLink Telecom", "UTF-8");

        Transport.send(mensaje);
    }
}
