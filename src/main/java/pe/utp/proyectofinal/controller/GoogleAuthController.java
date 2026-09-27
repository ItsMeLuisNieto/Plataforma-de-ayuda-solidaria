package pe.utp.proyectofinal.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.repository.UsuarioRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController // Usamos RestController porque respondemos a un fetch de JavaScript (JSON), no a un formulario tradicional
public class GoogleAuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String CLIENT_ID = "122831283261-9gc5ojcgeetnllp6dcj15b3vbiosshbt.apps.googleusercontent.com";

    @PostMapping("/login/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload googlePayload = idToken.getPayload();
                
                // Extraer datos de Google
                String email = googlePayload.getEmail();
                String nombre = (String) googlePayload.get("name"); 

                Usuario usuario = usuarioRepository.findByEmail(email);
                Map<String, Object> respuesta = new HashMap<>();

                if (usuario == null) {
                    // El usuario es nuevo, NO lo guardamos aún. 
                    // Le decimos al frontend que lo mande a completar su registro.
                    respuesta.put("accion", "completar_registro");
                    respuesta.put("email", email);
                    respuesta.put("nombre", nombre);
                } else {
                    // El usuario ya existe, iniciamos sesión normal
                    respuesta.put("accion", "login_exitoso");
                }

                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error del servidor"));
        }
    }
}