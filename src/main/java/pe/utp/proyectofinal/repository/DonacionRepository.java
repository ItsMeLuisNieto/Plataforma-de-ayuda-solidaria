package pe.utp.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.proyectofinal.entity.Donacion;

public interface DonacionRepository extends JpaRepository<Donacion, Long> {
    List<Donacion> findByEstado(String estado);
}
