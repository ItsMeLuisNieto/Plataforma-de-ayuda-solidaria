package pe.utp.proyectofinal.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiSecurityResponseWriter writer;

    public ApiAccessDeniedHandler(ApiSecurityResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        writer.escribir(response, HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta acción");
    }
}
