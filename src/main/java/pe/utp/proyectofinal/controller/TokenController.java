package pe.utp.proyectofinal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.proyectofinal.service.AuthService;

@RestController
public class TokenController {

    private final AuthService authService;

    public TokenController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/token")
    public ResponseEntity<?> obtenerToken(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debes iniciar sesión para realizar esta acción"));
        }
        return ResponseEntity.ok(Map.of(
                "token", authService.tokenPara(authentication.getName()),
                "tipo", "Bearer"));
    }
}
