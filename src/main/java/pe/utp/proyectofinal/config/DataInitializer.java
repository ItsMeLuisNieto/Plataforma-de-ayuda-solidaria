package pe.utp.proyectofinal.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.entity.RecepcionDonacion;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.DonacionRepository;
import pe.utp.proyectofinal.repository.RecepcionDonacionRepository;
import pe.utp.proyectofinal.repository.TestimonioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner cargarDatosIniciales(CampanaRepository campanaRepository,
                                                  TestimonioRepository testimonioRepository,
                                                  DonacionRepository donacionRepository,
                                                  RecepcionDonacionRepository recepcionDonacionRepository) {
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
                List<Campana> campanas = campanaRepository.findAll();
                Campana cAlimentos = campanas.stream()
                        .filter(c -> {
                            String nombre = c.getNombre() != null ? c.getNombre() : c.getTitulo();
                            return nombre != null && nombre.toLowerCase().contains("alimento");
                        })
                        .findFirst().orElse(!campanas.isEmpty() ? campanas.get(0) : null);
                Campana cRopa = campanas.stream()
                        .filter(c -> {
                            String nombre = c.getNombre() != null ? c.getNombre() : c.getTitulo();
                            return nombre != null && nombre.toLowerCase().contains("ropa");
                        })
                        .findFirst().orElse(cAlimentos);
                Campana cEscolar = campanas.stream()
                        .filter(c -> {
                            String nombre = c.getNombre() != null ? c.getNombre() : c.getTitulo();
                            return nombre != null && nombre.toLowerCase().contains("escolar");
                        })
                        .findFirst().orElse(cAlimentos);

                Donacion d1 = donacionRepository.save(crearDonacion("LOTE-1001", "Supermercado MegaPerú", "Arroz", 150, "CONFIRMADO", cAlimentos));
                Donacion d2 = donacionRepository.save(crearDonacion("LOTE-1002", "Textiles Andinos SAC", "Ropa infantil", 80, "CONFIRMADO", cRopa));
                Donacion d3 = donacionRepository.save(crearDonacion("LOTE-1003", "Panadería Dulce Vida", "Pan", 60, "CONFIRMADO", cAlimentos));
                Donacion d4 = donacionRepository.save(crearDonacion("LOTE-1004", "Farmacia Salud Total", "Productos de higiene", 45, "CONFIRMADO", cAlimentos));
                Donacion d5 = donacionRepository.save(crearDonacion("LOTE-1005", "Librería El Saber", "Cuadernos y útiles escolares", 120, "CONFIRMADO", cEscolar));
                Donacion d6 = donacionRepository.save(crearDonacion("LOTE-1006", "Comunidad Vecinal Los Olivos", "Leche en polvo", 90, "CONFIRMADO", cAlimentos));
                Donacion d7 = donacionRepository.save(crearDonacion("LOTE-1007", "Calzados Norte SAC", "Calzado escolar", 65, "CONFIRMADO", cRopa));

                if (recepcionDonacionRepository.count() == 0) {
                    crearRecepcion(d1, cAlimentos, recepcionDonacionRepository);
                    crearRecepcion(d2, cRopa, recepcionDonacionRepository);
                    crearRecepcion(d3, cAlimentos, recepcionDonacionRepository);
                    crearRecepcion(d4, cAlimentos, recepcionDonacionRepository);
                    crearRecepcion(d5, cEscolar, recepcionDonacionRepository);
                    crearRecepcion(d6, cAlimentos, recepcionDonacionRepository);
                    crearRecepcion(d7, cRopa, recepcionDonacionRepository);
                }
            }
        };
    }

    private Campana crearCampana(String titulo, String descripcion, int metaDias, int porcentaje,
                                 String ubicacion, int beneficiarios, int meta) {
        Campana campana = new Campana();
        campana.setNombre(titulo);
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

    private Donacion crearDonacion(String codigoLote, String nombreDonante, String producto, int cantidad, String estado, Campana campana) {
        Donacion donacion = new Donacion();
        donacion.setCodigoLote(codigoLote);
        donacion.setNombreDonante(nombreDonante);
        donacion.setProducto(producto);
        donacion.setCantidad(cantidad);
        donacion.setEstado(estado);
        donacion.setCampana(campana);
        return donacion;
    }

    private void crearRecepcion(Donacion donacion, Campana campana, RecepcionDonacionRepository recepcionDonacionRepository) {
        RecepcionDonacion recepcion = new RecepcionDonacion();
        recepcion.setDonacion(donacion);
        recepcion.setCampana(campana);
        recepcion.setEstado("CONFIRMADO");
        recepcion.setFechaRevision(java.time.LocalDateTime.now());
        recepcion.setRevisadoPor("Sistema");
        recepcionDonacionRepository.save(recepcion);
    }

}

