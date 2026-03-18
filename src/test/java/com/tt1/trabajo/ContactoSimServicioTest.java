package com.tt1.trabajo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import servicios.ContactoSimServicio;
import modelo.DatosSolicitud;
import modelo.DatosSimulation;
import modelo.Entidad;
import java.util.HashMap;
import java.util.List;

class ContactoSimServicioTest {

    private ContactoSimServicio servicio;

    @BeforeEach
    void setUp() {
        // el servicio requiere un logger para el constructor
        Logger mockLogger = Mockito.mock(Logger.class);
        servicio = new ContactoSimServicio(mockLogger);
    }

    @Test
    void testGetEntitiesGeneraExactamenteCinco() {
        // act
        List<Entidad> entidades = servicio.getEntities();
        
        // assert
        assertNotNull(entidades, "la lista no debe ser nula");
        assertEquals(5, entidades.size(), "el servicio debe generar exactamente 5 entidades");
        assertEquals("Entidad 0", entidades.get(0).getName(), "la primera entidad debe llamarse 'Entidad 0'");
    }

    @Test
    void testIsValidEntityIdSiempreRetornaTrue() {
        // según tu código, este método siempre devuelve true
        assertTrue(servicio.isValidEntityId(999));
        assertTrue(servicio.isValidEntityId(-1));
    }

    @Test
    void testSolicitarSimulationManejaErrorDeConexion() {
        // si la vm no está corriendo en el 8080, el try-catch devolverá -1
        DatosSolicitud sol = new DatosSolicitud(new HashMap<>());
        int token = servicio.solicitarSimulation(sol);
        
        // esto verifica que el catch funciona y no rompe la app
        assertTrue(token >= -1, "el token debe ser un id válido o -1 en caso de error");
    }

    @Test
    void testDescargarDatosRetornaObjetoVacioSiTicketInvalido() {
        // act
        DatosSimulation resultado = servicio.descargarDatos(-1);
        
        // assert
        assertNotNull(resultado, "siempre debe devolver un objeto DatosSimulation, nunca null");
        assertNull(resultado.getPuntos(), "un ticket inválido no debería tener mapa de puntos");
        assertEquals(0, resultado.getAnchoTablero(), "el ancho por defecto debe ser 0");
    }
}