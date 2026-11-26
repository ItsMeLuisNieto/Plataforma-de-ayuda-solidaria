package pe.utp.proyectofinal.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import pe.utp.proyectofinal.dto.GoogleUserDTO;

@Service
public class GoogleAuthService {

    private final String clientId;

    public GoogleAuthService(@Value("${app.google.client-id}") String clientId) {
        this.clientId = clientId;
    }

    public GoogleUserDTO verificar(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken == null) {
                return null;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            return new GoogleUserDTO(payload.getEmail(), (String) payload.get("name"));
        } catch (Exception ex) {
            return null;
        }
    }
}
