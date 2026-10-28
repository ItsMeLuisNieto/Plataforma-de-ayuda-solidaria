package pe.utp.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.proyectofinal.entity.RecepcionDonacion;

public interface RecepcionDonacionRepository extends JpaRepository<RecepcionDonacion, Long> {
    List<RecepcionDonacion> findAllByOrderByFechaSolicitudDesc();
    List<RecepcionDonacion> findByEstadoOrderByFechaSolicitudAsc(String estado);
}
