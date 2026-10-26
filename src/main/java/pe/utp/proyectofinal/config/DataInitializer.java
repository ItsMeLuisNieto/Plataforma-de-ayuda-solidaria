package pe.utp.proyectofinal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.entity.Testimonio;
import pe.utp.proyectofinal.repository.CampanaRepository;
import pe.utp.proyectofinal.repository.TestimonioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner cargarDatosIniciales(CampanaRepository campanaRepository,
                                                  TestimonioRepository testimonioRepository) {
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
}
