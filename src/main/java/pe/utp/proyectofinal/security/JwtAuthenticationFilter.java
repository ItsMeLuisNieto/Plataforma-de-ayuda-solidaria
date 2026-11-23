package pe.utp.proyectofinal.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.utp.proyectofinal.config.ApiAuthenticationEntryPoint;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String CABECERA = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ApiAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService,
                                   ApiAuthenticationEntryPoint entryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String cabecera = request.getHeader(CABECERA);
        if (cabecera == null || !cabecera.startsWith(PREFIJO)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cabecera.substring(PREFIJO.length()).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtService.extraerSubject(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (!userDetails.isEnabled() || !jwtService.esValido(token, userDetails)) {
                    rechazar(request, response);
                    return;
                }
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException ex) {
            rechazar(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            rechazar(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void rechazar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        entryPoint.commence(request, response, new BadCredentialsException("Token inválido"));
    }
}
