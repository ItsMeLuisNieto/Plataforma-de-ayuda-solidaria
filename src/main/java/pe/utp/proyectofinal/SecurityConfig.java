package pe.utp.proyectofinal; // Asegúrate de que coincida con tu paquete

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // Protege estas rutas para que exija sesión iniciada
                .requestMatchers("/donar-ahora", "/ver-voluntario", "/donar-bienes", "/eventos", "/ver-campanas").authenticated() 
                // El resto de páginas (login, registro, index) siguen siendo públicas
                .anyRequest().permitAll() 
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") // El 'action' del <form>
                .usernameParameter("email") // El 'name' del input del correo
                .passwordParameter("pass") // El 'name' del input de la contraseña
                .defaultSuccessUrl("/donar-ahora", true) // A dónde redirigir si el login es correcto
                .failureUrl("/login?error=incorrect_credentials") // A dónde redirigir si se equivoca
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/kallpa-solidary") // A dónde va al cerrar sesión
                .permitAll()
            );
            
        return http.build();
    }
}