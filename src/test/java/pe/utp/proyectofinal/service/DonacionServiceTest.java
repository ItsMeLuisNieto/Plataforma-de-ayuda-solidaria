package pe.utp.proyectofinal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.utp.proyectofinal.dto.DonacionRequest;
import pe.utp.proyectofinal.entity.Donacion;
import pe.utp.proyectofinal.repository.DonacionRepository;

@ExtendWith(MockitoExtension.class)
class DonacionServiceTest {

    @Mock
    private DonacionRepository donacionRepository;

    @InjectMocks
    private DonacionService donacionService;

    @Test
    void registraDonacionConEstadoPorDefecto() {
        when(donacionRepository.save(any(Donacion.class))).thenAnswer(invocation -> {
            Donacion donacion = invocation.getArgument(0);
            donacion.setId(10L);
            return donacion;
        });

        DonacionRequest request = new DonacionRequest();
        request.setNombreDonante("Ana Pérez");
        request.setProducto("Abrigos");
        request.setCantidad(5);

        Donacion donacion = donacionService.registrar(request);

        assertEquals(10L, donacion.getId());
        assertEquals("Registrado", donacion.getEstado());
        assertEquals(5, donacion.getCantidad());
    }
}
