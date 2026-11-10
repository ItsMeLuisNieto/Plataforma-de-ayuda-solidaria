package pe.utp.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.model.EnvioDonacion;

public interface EnvioDonacionRepository extends JpaRepository<EnvioDonacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"donacion", "campana", "campana.usuario"})
    Optional<EnvioDonacion> findById(Long id);

    @EntityGraph(attributePaths = {"donacion", "campana", "campana.usuario"})
    List<EnvioDonacion> findAllByOrderByFechaRegistroDesc();

    @EntityGraph(attributePaths = {"donacion", "campana", "campana.usuario"})
    List<EnvioDonacion> findByEstadoOrderByFechaRegistroAsc(String estado);

    @EntityGraph(attributePaths = {"donacion", "campana", "campana.usuario"})
    List<EnvioDonacion> findByCampanaId(Long campanaId);

    @EntityGraph(attributePaths = {"donacion", "campana", "campana.usuario"})
    Optional<EnvioDonacion> findByDonacionId(Long donacionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM EnvioDonacion e WHERE e.donacion.id = :donacionId")
    void deleteByDonacionId(@Param("donacionId") Long donacionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM EnvioDonacion e WHERE e.campana.id = :campanaId OR e.donacion.campana.id = :campanaId")
    void deleteByCampanaId(@Param("campanaId") Long campanaId);
}
