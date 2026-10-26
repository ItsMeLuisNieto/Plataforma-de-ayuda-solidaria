package pe.utp.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.utp.proyectofinal.dto.RegistroRequest;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void rechazaCorreoDuplicado() {
        when(usuarioRepository.existsByEmail("ana@correo.com")).thenReturn(true);

        RegistroRequest request = request("ana@correo.com", "secreto123", "secreto123");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> usuarioService.registrar(request));

        assertEquals("email_exists", exception.getCode());
    }

    @Test
    void rechazaContrasenasQueNoCoinciden() {
        when(usuarioRepository.existsByEmail("ana@correo.com")).thenReturn(false);

        RegistroRequest request = request("ana@correo.com", "secreto123", "otraclave");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> usuarioService.registrar(request));

        assertEquals("pass_mismatch", exception.getCode());
    }

    @Test
    void registraUsuarioConPasswordEncriptado() {
        when(usuarioRepository.existsByEmail("ana@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto123")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        RegistroRequest request = request("ana@correo.com", "secreto123", "secreto123");

        Usuario usuario = usuarioService.registrar(request);

        assertEquals(1L, usuario.getId());
        assertEquals("ana@correo.com", usuario.getEmail());
        assertEquals("USER", usuario.getRol());
    }

    private RegistroRequest request(String email, String password, String confirmPassword) {
        RegistroRequest request = new RegistroRequest();
        request.setEmail(email);
        request.setNombre("Ana Pérez");
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return request;
    }
}
