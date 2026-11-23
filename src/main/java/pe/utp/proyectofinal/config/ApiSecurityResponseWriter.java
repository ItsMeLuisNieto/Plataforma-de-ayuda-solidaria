package pe.utp.proyectofinal.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiSecurityResponseWriter {

    public void escribir(HttpServletResponse response, HttpStatus estado, String mensaje) throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String seguro = mensaje.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write("{\"mensaje\": \"" + seguro + "\"}");
    }
}
