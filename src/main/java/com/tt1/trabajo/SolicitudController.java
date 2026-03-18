package com.tt1.trabajo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import interfaces.InterfazContactoSim;
import modelo.DatosSolicitud;

@Controller
public class SolicitudController {
	
	private final InterfazContactoSim ics;
	private final Logger logger;
	
	public SolicitudController(InterfazContactoSim ics, Logger logger) {
		this.ics = ics;
		this.logger = logger;
	}

	@GetMapping("/solicitud")
	public String solicitud(Model model) {
		model.addAttribute("entities", ics.getEntities());
		return "solicitud";
	}
	
	@PostMapping("/solicitud")
	public String handleSolicitud(@RequestParam Map<String, String> formData, Model model) {
		Map<Integer, Integer> validData = new HashMap<>();
		List<String> errors = new ArrayList<>();

		formData.forEach((key, value) -> {
			try {
				// el formulario envía los id de las entidades como nombres de los campos (key)
				int id = Integer.parseInt(key);
				int num = Integer.parseInt(value);
				
				if (num < 0) {
					errors.add("el valor para la entidad " + key + " no puede ser negativo");
				} else if (ics.isValidEntityId(id)) {
					validData.put(id, num);
				} else {
					errors.add("id de entidad no válido: " + key);
				}
			} catch (NumberFormatException e) {
				// ignoramos campos que no sean numéricos (como el botón de submit si viaja en el mapa)
				if (!key.equals("_csrf")) { 
					logger.debug("campo no numérico ignorado: " + key);
				}
			}
		});

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			logger.warn("atendida petición con errores de validación");
		} else {
			logger.info("atendida petición de simulación correctamente");
			DatosSolicitud ds = new DatosSolicitud(validData);
			int tok = ics.solicitarSimulation(ds);
			
			if (tok != -1) {
				model.addAttribute("token", tok);
			} else {
				model.addAttribute("errorServer", "no se pudo conectar con el servidor de simulación");
				logger.error("error en comunicación con servidor de simulación (token -1)");
			}
		}
		return "formResult";
	}
}