package pe.utp.proyectofinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import pe.utp.proyectofinal.service.UsuarioService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioService usuarioService;
    private final String googleClientId;

    public GlobalControllerAdvice(UsuarioService usuarioService,
                                  @Value("${app.google.client-id}") String googleClientId) {
        this.usuarioService = usuarioService;
        this.googleClientId = googleClientId;
    }

    @ModelAttribute("nombreUsuario")
    public String inyectarNombreGlobal(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return usuarioService.buscarPorEmail(authentication.getName())
                    .map(usuario -> usuario.getNombre())
                    .orElse(null);
        }
        return null;
    }

    @ModelAttribute("googleClientId")
    public String googleClientId() {
        return googleClientId;
    }
}
