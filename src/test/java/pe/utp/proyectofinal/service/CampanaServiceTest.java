package pe.utp.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.utp.proyectofinal.dto.CampanaRequest;
import pe.utp.proyectofinal.entity.Campana;
import pe.utp.proyectofinal.repository.CampanaRepository;

@ExtendWith(MockitoExtension.class)
class CampanaServiceTest {

    @Mock
    private CampanaRepository campanaRepository;

    @InjectMocks
    private CampanaService campanaService;

    @Test
    void normalizaDiasYMetaAlCrear() {
        when(campanaRepository.save(any(Campana.class))).thenAnswer(invocation -> {
            Campana campana = invocation.getArgument(0);
            campana.setId(1L);
            return campana;
        });

        CampanaRequest request = new CampanaRequest();
        request.setTitulo("Abrigo para todos");
        request.setDescripcion("Campaña de invierno");
        request.setMetaDias("15");
        request.setPorcentaje(20);
        request.setUbicacion("Los Olivos");
        request.setBeneficiarios("120 familias");
        request.setMeta("500 abrigos");

        Campana campana = campanaService.crear(request);

        assertEquals(1L, campana.getId());
        assertEquals("15 días", campana.getMetaDias());
        assertEquals("Meta: 500 abrigos", campana.getMeta());
    }
}
