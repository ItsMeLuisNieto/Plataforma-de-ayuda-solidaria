package pe.utp.proyectofinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.proyectofinal.entity.Donacion;

public interface DonacionRepository extends JpaRepository<Donacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"campana", "campana.usuario"})
    List<Donacion> findAll();

    @Override
    @EntityGraph(attributePaths = {"campana", "campana.usuario"})
    Optional<Donacion> findById(Long id);

    @EntityGraph(attributePaths = {"campana", "campana.usuario"})
    List<Donacion> findByEstado(String estado);

    @EntityGraph(attributePaths = {"campana", "campana.usuario"})
    List<Donacion> findByCampanaId(Long campanaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Donacion d WHERE d.campana.id = :campanaId")
    void deleteByCampanaId(@Param("campanaId") Long campanaId);
}
