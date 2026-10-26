package pe.utp.proyectofinal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import pe.utp.proyectofinal.dto.GoogleUserDTO;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.service.GoogleAuthService;
import pe.utp.proyectofinal.service.UsuarioService;

@RestController
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final UsuarioService usuarioService;
    private final UserDetailsService userDetailsService;

    public GoogleAuthController(GoogleAuthService googleAuthService,
                                UsuarioService usuarioService,
                                UserDetailsService userDetailsService) {
        this.googleAuthService = googleAuthService;
        this.usuarioService = usuarioService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> payload,
                                            HttpServletRequest request) {
        GoogleUserDTO googleUser = googleAuthService.verificar(payload.get("token"));
        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido"));
        }

        Usuario usuario = usuarioService.buscarPorEmail(googleUser.email()).orElse(null);
        Map<String, Object> respuesta = new HashMap<>();

        if (usuario == null) {
            respuesta.put("accion", "completar_registro");
            respuesta.put("email", googleUser.email());
            respuesta.put("nombre", googleUser.nombre());
            return ResponseEntity.ok(respuesta);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        respuesta.put("accion", "login_exitoso");
        return ResponseEntity.ok(respuesta);
    }
}
