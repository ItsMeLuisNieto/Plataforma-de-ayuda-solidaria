package pe.utp.proyectofinal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.entity.SolicitudAyuda;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.SolicitudAyudaRepository;
import pe.utp.proyectofinal.repository.TestimonioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner cargarDatosIniciales(CampanaRepository campanaRepository,
                                                  TestimonioRepository testimonioRepository,
                                                  DonacionRepository donacionRepository,
                                                  SolicitudAyudaRepository solicitudAyudaRepository) {
        return args -> {
            if (campanaRepository.count() == 0) {
                campanaRepository.save(crearCampana(
                        "Canasta de alimentos",
                        "Aportamos kits básicos para familias vulnerables.",
                        7, 12, "San Martín de Porres", 240, 18000));
                campanaRepository.save(crearCampana(
                        "Ropa y abrigo",
                        "Recibimos prendas, mochilas y calzado para niños.",
                        14, 58, "El Agustino", 180, 1300));
                campanaRepository.save(crearCampana(
                        "Material escolar",
                        "Apoyamos a estudiantes con cuadernos y uniformes.",
                        21, 81, "Surco", 310, 24500));
            }

            if (testimonioRepository.count() == 0) {
                testimonioRepository.save(crearTestimonio(
                        "Luna Torres", "Donante",
                        "Increíble la transparencia con la que entregan la ayuda humanitaria en cada campaña."));
                testimonioRepository.save(crearTestimonio(
                        "Enrique Lee", "Voluntario",
                        "Ser parte de los voluntariados me ha permitido ver el impacto real en las comunidades."));
            }

            if (donacionRepository.count() == 0) {
                donacionRepository.save(crearDonacion("LOTE-1001", "Supermercado MegaPerú", "Arroz", 150, "Recibido"));
                donacionRepository.save(crearDonacion("LOTE-1002", "Textiles Andinos SAC", "Ropa infantil", 80, "Recibido"));
                donacionRepository.save(crearDonacion("LOTE-1003", "Panadería Dulce Vida", "Pan", 60, "En proceso"));
                donacionRepository.save(crearDonacion("LOTE-1004", "Farmacia Salud Total", "Productos de higiene", 45, "Recibido"));
                donacionRepository.save(crearDonacion("LOTE-1005", "Librería El Saber", "Cuadernos y útiles escolares", 120, "En proceso"));
                donacionRepository.save(crearDonacion("LOTE-1006", "Comunidad Vecinal Los Olivos", "Leche en polvo", 90, "Entregado"));
                donacionRepository.save(crearDonacion("LOTE-1007", "Calzados Norte SAC", "Calzado escolar", 65, "Entregado"));
            }

            if (solicitudAyudaRepository.count() == 0) {
                solicitudAyudaRepository.save(crearSolicitudAyuda("Familia Pérez", "Alimentos", "Necesitan canasta básica para 5 personas."));
                solicitudAyudaRepository.save(crearSolicitudAyuda("Comedor Comunitario Luz", "Ropa y abrigo", "Requieren mantas y prendas para adultos mayores."));
                solicitudAyudaRepository.save(crearSolicitudAyuda("Asociación Sembrando Esperanza", "Material escolar", "Solicitan útiles escolares para 30 niños."));
                solicitudAyudaRepository.save(crearSolicitudAyuda("Hogar San José", "Productos de higiene", "Necesitan jabón, papel higiénico y productos de limpieza."));
                solicitudAyudaRepository.save(crearSolicitudAyuda("Centro Infantil Rayito", "Calzado", "Solicitan zapatos escolares para niños de primaria."));
            }
        };
    }

    private Campana crearCampana(String titulo, String descripcion, int metaDias, int porcentaje,
                                 String ubicacion, int beneficiarios, int meta) {
        Campana campana = new Campana();
        campana.setTitulo(titulo);
        campana.setDescripcion(descripcion);
        campana.setMetaDias(metaDias);
        campana.setPorcentaje(porcentaje);
        campana.setUbicacion(ubicacion);
        campana.setBeneficiarios(beneficiarios);
        campana.setMeta(meta);
        return campana;
    }

    private Testimonio crearTestimonio(String nombre, String rol, String comentario) {
        Testimonio testimonio = new Testimonio();
        testimonio.setNombre(nombre);
        testimonio.setRol(rol);
        testimonio.setComentario(comentario);
        return testimonio;
    }

    private Donacion crearDonacion(String codigoLote, String nombreDonante, String producto, int cantidad, String estado) {
        Donacion donacion = new Donacion();
        donacion.setCodigoLote(codigoLote);
        donacion.setNombreDonante(nombreDonante);
        donacion.setProducto(producto);
        donacion.setCantidad(cantidad);
        donacion.setEstado(estado);
        return donacion;
    }

    private SolicitudAyuda crearSolicitudAyuda(String nombreDonantes, String tipoAyuda, String descripcion) {
        SolicitudAyuda solicitud = new SolicitudAyuda();
        solicitud.setNombreDonantes(nombreDonantes);
        solicitud.setTipoAyuda(tipoAyuda);
        solicitud.setDescripcion(descripcion);
        solicitud.setEstado("Registrado");
        return solicitud;
    }
}
