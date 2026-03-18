package com.tt1.trabajo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.mockito.Mockito;
import servicios.EnviarEmailsServicio;
import modelo.Destinatario;

class EnviarEmailsServicioTest {
    @Test
    void testEnviarEmailRetornaTrue() {
        // 1. mockeamos el logger
        Logger mockLogger = Mockito.mock(Logger.class);
        
        // 2. creamos el servicio
        EnviarEmailsServicio servicio = new EnviarEmailsServicio(mockLogger);
        
        // 3. creamos un destinatario válido
        Destinatario d = new Destinatario("usuario prueba", "test@test.com");
        
        // 4. ejecutamos y comprobamos
        assertTrue(servicio.enviarEmail(d, d.getEmail()));
    }
}