package pe.utp.proyectofinal.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiSecurityResponseWriter writer;

    public ApiAuthenticationEntryPoint(ApiSecurityResponseWriter writer) {
        this.writer = writer;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writer.escribir(response, HttpStatus.UNAUTHORIZED,
                "Debes iniciar sesión para realizar esta acción");
    }
}
