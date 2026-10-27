package pe.utp.proyectofinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.proyectofinal.entity.Donacion;

public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    Optional<Donacion> findByNroOperacion(String nroOperacion);
}