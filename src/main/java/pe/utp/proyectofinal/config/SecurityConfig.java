package pe.utp.proyectofinal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para endpoints de la API REST para permitir pruebas con Thunder Client/Postman
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                // Endpoints REST de la API permitidos para pruebas libres en Thunder Client
                .requestMatchers("/api/**").permitAll()
                // Vistas y acciones web que requieren autenticación
                .requestMatchers("/donar-ahora", "/ver-voluntario", "/donar-bienes",
                        "/eventos", "/ver-campanas").authenticated()
                .requestMatchers(HttpMethod.POST, "/campanas/**").authenticated()
                .anyRequest().permitAll()
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
}
