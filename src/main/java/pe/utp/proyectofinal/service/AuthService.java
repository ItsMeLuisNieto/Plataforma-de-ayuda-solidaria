package pe.utp.proyectofinal.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import pe.utp.proyectofinal.dto.AuthResponse;
import pe.utp.proyectofinal.dto.LoginRequest;
import pe.utp.proyectofinal.dto.RegistroRequest;
import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioService usuarioService,
                       UserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        UserDetails userDetails = (UserDetails) autenticacion.getPrincipal();
        validarActivo(userDetails);
        return construirRespuesta(userDetails);
    }

    public AuthResponse registrar(RegistroRequest request) {
        Usuario usuario = usuarioService.registrar(request);
        return construirRespuesta(userDetailsService.loadUserByUsername(usuario.getEmail()));
    }

    public String tokenPara(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        validarActivo(userDetails);
        return jwtService.generarToken(userDetails);
    }

    private void validarActivo(UserDetails userDetails) {
        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    private AuthResponse construirRespuesta(UserDetails userDetails) {
        String rol = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("")
                .replaceFirst("^ROLE_", "");
        return new AuthResponse(
                jwtService.generarToken(userDetails),
                "Bearer",
                jwtService.expiracionMilisegundos(),
                userDetails.getUsername(),
                rol);
    }
}
