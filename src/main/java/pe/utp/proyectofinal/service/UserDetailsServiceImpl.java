package pe.utp.proyectofinal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.utp.proyectofinal.model.Usuario;
import pe.utp.proyectofinal.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword()) // Spring comparará esto automáticamente con lo que tecleó el usuario
                .roles(usuario.getRol()) // Asigna el rol (ej. "USER")
                .build();
    }
}