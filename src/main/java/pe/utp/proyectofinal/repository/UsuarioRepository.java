package pe.utp.proyectofinal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.proyectofinal.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Con solo nombrar el método así, Spring Boot genera el SQL:
    // SELECT * FROM usuarios WHERE email = ?
    Usuario findByEmail(String email);
    
    boolean existsByEmail(String email);
}