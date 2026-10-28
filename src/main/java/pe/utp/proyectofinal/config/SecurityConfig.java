package pe.utp.proyectofinal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/donar-ahora", "/ver-voluntario", "/donar-bienes",
                        "/eventos", "/ver-campanas").authenticated()
                .requestMatchers(HttpMethod.POST, "/campanas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/donaciones", "/api/recepcion-donaciones/**").authenticated()
                .anyRequest().permitAll()
            )
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
