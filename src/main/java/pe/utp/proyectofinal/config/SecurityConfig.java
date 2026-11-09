package pe.utp.proyectofinal.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, authException) -> escribirJson(response,
                HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión para realizar esta acción");
    }

    @Bean
    public AccessDeniedHandler apiAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> escribirJson(response,
                HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para realizar esta acción");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para endpoints de la API REST para permitir pruebas con Thunder Client/Postman
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                // Lectura pública de la API para todos
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                // Testimonios: usuarios autenticados publican y editan los suyos (propiedad validada en el servicio)
                .requestMatchers(HttpMethod.POST, "/api/testimonios").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/testimonios/**").authenticated()
                // Resto de escritura en la API: solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                // Vistas y acciones web que requieren autenticación
                .requestMatchers("/donar-ahora", "/ver-voluntario", "/donar-bienes",
                        "/eventos", "/ver-campanas").authenticated()
                // Escritura de campañas web (crear/eliminar): solo ADMIN
                .requestMatchers(HttpMethod.POST, "/campanas/**").hasRole("ADMIN")
                // Login con Google (POST /login/google), registro y páginas públicas
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception
                // /api/** responde 401/403 en JSON en lugar de redirigir a la vista de login
                .defaultAuthenticationEntryPointFor(
                    apiAuthenticationEntryPoint(),
                    request -> request.getRequestURI().startsWith("/api/"))
                .defaultAccessDeniedHandlerFor(
                    apiAccessDeniedHandler(),
                    request -> request.getRequestURI().startsWith("/api/"))
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("pass")
                .defaultSuccessUrl("/donar-ahora", true)
                .failureUrl("/login?error=incorrect_credentials")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/kallpa-solidary")
                .permitAll()
            );

        return http.build();
    }

    private void escribirJson(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"mensaje\": \"" + mensaje + "\"}");
    }
}
