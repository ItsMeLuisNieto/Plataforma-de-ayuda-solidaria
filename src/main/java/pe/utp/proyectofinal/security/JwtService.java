package pe.utp.proyectofinal.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String generarToken(UserDetails userDetails) {
        Instant ahora = Instant.now();
        String rol = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("rol", rol)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(properties.expirationMillis())))
                .signWith(properties.signingKey())
                .compact();
    }

    public long expiracionMilisegundos() {
        return properties.expirationMillis();
    }

    public String extraerSubject(String token) {
        return parsear(token).getPayload().getSubject();
    }

    public boolean esValido(String token, UserDetails userDetails) {
        Claims claims = parsear(token).getPayload();
        return userDetails.getUsername().equals(claims.getSubject())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parser()
                .verifyWith(properties.signingKey())
                .build()
                .parseSignedClaims(token);
    }
}
