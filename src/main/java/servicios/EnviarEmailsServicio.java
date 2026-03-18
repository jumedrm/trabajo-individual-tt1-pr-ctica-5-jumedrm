package servicios;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;

@Service
public class EnviarEmailsServicio implements InterfazEnviarEmails {

    private final Logger logger;

    public EnviarEmailsServicio(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean enviarEmail(Destinatario dest, String email) {
        try {
            // construimos el cuerpo del mensaje simulado
            String cuerpo = "estimado " + dest.getNombre() + ", su simulación ha sido procesada correctamente.";
            
            // simulamos el envío por el log
            logger.info("================================================");
            logger.info("enviando notificación por email...");
            logger.info("destinatario: " + dest.getNombre());
            logger.info("email de destino: " + email);
            logger.info("mensaje: " + cuerpo);
            logger.info("================================================");
            
            return true; // indicamos que el "envío" ha sido un éxito
            
        } catch (Exception e) {
            logger.error("error al simular el envío de email: " + e.getMessage());
            return false;
        }
    }
}