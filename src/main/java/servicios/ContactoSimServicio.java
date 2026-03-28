package servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.openapitools.jackson.nullable.JsonNullableModule;

import com.tt1.trabajo.utilidades.ApiClient;
import com.tt1.trabajo.utilidades.api.ResultadosApi;
import com.tt1.trabajo.utilidades.api.SolicitudApi;
import com.tt1.trabajo.utilidades.model.ResultsResponse;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;

@Service
public class ContactoSimServicio implements InterfazContactoSim {

    private final SolicitudApi solicitudApi;
    private final ResultadosApi resultadosApi;
    private final Logger logger;

    public ContactoSimServicio(Logger logger) {
        this.logger = logger;

        // 1. configurar el resttemplate con el parche de jsonnullable
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(MappingJackson2HttpMessageConverter.class::cast)
            .forEach(c -> c.getObjectMapper().registerModule(new JsonNullableModule()));

        // 2. inicializar el apiclient generado
        ApiClient apiClient = new ApiClient(restTemplate);
        //apiClient.setBasePath("http://localhost:8080");//de normal uso esta
        //apiClient.setBasePath("http://host.docker.internal:8080");//para Docker uso esta
        apiClient.setBasePath("http://servicio-consumible:8080"); //para la práctica 7 la parte individual
        this.solicitudApi = new SolicitudApi(apiClient);
        this.resultadosApi = new ResultadosApi(apiClient);
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        try {
            com.tt1.trabajo.utilidades.model.Solicitud apiSol = new com.tt1.trabajo.utilidades.model.Solicitud();
            
            List<Integer> listaCantidades = new ArrayList<>();
            List<String> listaNombres = new ArrayList<>();

            // vamos a rellenar AMBAS listas con la misma longitud
            // usamos 5 porque es lo que pusimos en getEntities()
            for (int i = 0; i < 5; i++) {
                listaCantidades.add(sol.getNums().getOrDefault(i, 0));
                listaNombres.add("Entidad " + i); // le damos un nombre genérico
            }
            
            // asignamos las dos listas al objeto que viaja a la VM
            apiSol.setCantidadesIniciales(listaCantidades);
            apiSol.setNombreEntidades(listaNombres); // <-- ESTA ES LA CLAVE

            var response = solicitudApi.solicitudSolicitarPost("jumedrm", apiSol);
            
            return (response != null && response.getTokenSolicitud() != null) ? response.getTokenSolicitud() : -1;
            
        } catch (Exception e) {
            // esto es lo que hemos visto en la consola
            logger.error("error al solicitar simulación: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        DatosSimulation ds = new DatosSimulation();
        try {
            ResultsResponse res = resultadosApi.resultadosPost("jumedrm", ticket);
            
            if (res != null && res.getData() != null && !res.getData().isEmpty()) {
                // usamos un split más agresivo por si acaso
                String[] lineas = res.getData().split("\n");
                
                // 1. el ancho del tablero (limpiamos posibles espacios)
                int ancho = Integer.parseInt(lineas[0].trim());
                ds.setAnchoTablero(ancho);
                
                Map<Integer, List<Punto>> mapaPuntos = new HashMap<>();
                int maxSeg = 0;

                // 2. procesamos cada línea de coordenadas
                for (int i = 1; i < lineas.length; i++) {
                    String linea = lineas[i].trim();
                    if (linea.isEmpty()) continue;
                    
                    String[] partes = linea.split(",");
                    if (partes.length == 4) {
                        int t = Integer.parseInt(partes[0]);
                        Punto p = new Punto();
                        // ajustamos según el orden del swagger: tiempo, y, x, color
                        p.setY(Integer.parseInt(partes[1]));
                        p.setX(Integer.parseInt(partes[2]));
                        p.setColor(partes[3]);

                        mapaPuntos.computeIfAbsent(t, k -> new ArrayList<>()).add(p);
                        if (t > maxSeg) maxSeg = t;
                    }
                }
                ds.setPuntos(mapaPuntos);
                ds.setMaxSegundos(maxSeg + 1); // para que el slider llegue hasta el final
                
                logger.info("simulación descargada: " + maxSeg + " segundos detectados.");
            }
        } catch (Exception e) {
            logger.error("error al procesar los datos del grid: " + e.getMessage());
           // e.printStackTrace();
        }
        return ds;
    }

    @Override
    public List<Entidad> getEntities() {
        List<Entidad> lista = new ArrayList<>();
        // creamos 5 entidades de prueba para que el formulario tenga campos
        for (int i = 0; i < 5; i++) {
            Entidad e = new Entidad();
            e.setId(i);
            e.setName("Entidad " + i);
            e.setDescripcion("Simulación de la entidad número " + i);
            lista.add(e);
        }
        return lista;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return true; // validación simple para que el controlador acepte los ids
    }
}