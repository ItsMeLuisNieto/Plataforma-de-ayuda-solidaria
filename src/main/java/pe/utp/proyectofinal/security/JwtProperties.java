package pe.utp.proyectofinal.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtProperties {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtProperties(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.expiration}") long expirationMillis) {
        final byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("jwt.secret debe estar codificado en Base64", ex);
        }
        if (bytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret debe tener al menos 32 bytes");
        }
        if (expirationMillis <= 0) {
            throw new IllegalArgumentException("jwt.expiration debe ser un valor positivo en milisegundos");
        }
        this.signingKey = Keys.hmacShaKeyFor(bytes);
        this.expirationMillis = expirationMillis;
    }

    public SecretKey signingKey() {
        return signingKey;
    }

    public long expirationMillis() {
        return expirationMillis;
    }
}
