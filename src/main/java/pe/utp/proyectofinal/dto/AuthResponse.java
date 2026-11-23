package pe.utp.proyectofinal.dto;

public record AuthResponse(String token, String tipo, long expiraEnMilisegundos,
                           String email, String rol) {
}
