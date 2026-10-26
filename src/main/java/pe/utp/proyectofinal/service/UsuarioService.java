package pe.utp.proyectofinal.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.dto.RegistroRequest;
import pe.utp.proyectofinal.entity.Usuario;
import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.exception.NotFoundException;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("email_exists", "El correo electrónico ya está registrado");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("pass_mismatch", "Las contraseñas no coinciden");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail().trim().toLowerCase());
        usuario.setNombre(request.getNombre().trim());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("USER");

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerActual(String email) {
        return obtenerPorEmail(email);
    }
}
