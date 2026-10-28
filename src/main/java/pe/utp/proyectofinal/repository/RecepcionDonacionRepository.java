package pe.utp.proyectofinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.entity.RecepcionDonacion;

public interface RecepcionDonacionRepository extends JpaRepository<RecepcionDonacion, Long> {

    @EntityGraph(attributePaths = {"donacion", "campana"})
    List<RecepcionDonacion> findAllByOrderByFechaSolicitudDesc();

    @EntityGraph(attributePaths = {"donacion", "campana"})
    List<RecepcionDonacion> findByEstadoOrderByFechaSolicitudAsc(String estado);
    List<RecepcionDonacion> findByDonacionId(Long donacionId);
    List<RecepcionDonacion> findByCampanaId(Long campanaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecepcionDonacion r WHERE r.donacion.id = :donacionId")
    void deleteByDonacionId(@Param("donacionId") Long donacionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecepcionDonacion r WHERE r.campana.id = :campanaId OR r.donacion.campana.id = :campanaId")
    void deleteByCampanaId(@Param("campanaId") Long campanaId);
}
