package pe.utp.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.proyectofinal.entity.Testimonio;

public interface TestimonioRepository extends JpaRepository<Testimonio, Long> {

    @Override
    @EntityGraph(attributePaths = "usuario")
    List<Testimonio> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<Testimonio> findById(Long id);
}
