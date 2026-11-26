package pe.utp.proyectofinal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Expone el token CSRF de la sesión para el frontend React (SPA).
 * La vista de Thymeleaf lo inyectaba mediante meta tags; al servir la SPA desde
 * Vite no existen esos meta tags, por eso se provee este endpoint.
 */
@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> obtenerToken(HttpServletRequest request) {
        Object atributo = request.getAttribute(CsrfToken.class.getName());

        if (!(atributo instanceof CsrfToken token)) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "No se pudo generar el token CSRF"));
        }

        return ResponseEntity.ok(Map.of(
                "token", token.getToken(),
                "headerName", token.getHeaderName()));
    }
}
