package pe.utp.proyectofinal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import pe.utp.proyectofinal.repository.UsuarioRepository;
import pe.utp.proyectofinal.security.JwtProperties;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void loginDevuelveToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@kallpa.local\",\"password\":\"kallpa123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"token\"")))
                .andExpect(content().string(containsString("\"rol\":\"ADMIN\"")));
    }

    @Test
    void loginCuentasSemillaNuevasDevuelveToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin123@kallpa.local\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"rol\":\"ADMIN\"")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario123@kallpa.local\",\"password\":\"usuario123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"rol\":\"USUARIO\"")));
    }

    @Test
    void loginInvalidoDevuelve401Generico() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no.existe@kallpa.local\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Credenciales inválidas")));
    }

    @Test
    void registroNoPermiteAsignarRolAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nuevo@kallpa.local\",\"nombre\":\"Nuevo Usuario\","
                                + "\"password\":\"clave12345\",\"confirmPassword\":\"clave12345\","
                                + "\"rol\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"rol\":\"USUARIO\"")));

        assertEquals("USUARIO", usuarioRepository.findByEmail("nuevo@kallpa.local").orElseThrow().getRol());
    }

    @Test
    void escrituraApiSinTokenDevuelve401Json() throws Exception {
        mockMvc.perform(post("/api/campanas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Campaña prueba\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"mensaje\"")));
    }

    @Test
    void usuarioNormalNoPuedeEscribirEnApi() throws Exception {
        String token = token("@kallpa.local");
        mockMvc.perform(post("/api/campanas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Campaña prueba\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenExpiradoDevuelve401() throws Exception {
        String expirado = Jwts.builder()
                .subject("admin@kallpa.local")
                .issuedAt(Date.from(Instant.now().minusSeconds(120)))
                .expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(jwtProperties.signingKey())
                .compact();
        mockMvc.perform(get("/api/usuario/actual")
                        .header("Authorization", "Bearer " + expirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPublicoSigueDisponibleSinToken() throws Exception {
        mockMvc.perform(get("/api/campanas"))
                .andExpect(status().isOk());
    }

    private String token(String sufijo) throws Exception {
        String email = "maria.quispe" + sufijo;
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"kallpa123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        int inicio = respuesta.indexOf("\"token\":\"") + 9;
        int fin = respuesta.indexOf('"', inicio);
        return respuesta.substring(inicio, fin);
    }
}
