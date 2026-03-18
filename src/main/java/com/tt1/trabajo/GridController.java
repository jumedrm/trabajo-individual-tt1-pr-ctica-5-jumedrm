package com.tt1.trabajo;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.Punto;

@Controller
public class GridController {
    private final InterfazContactoSim ics;
    private final Logger logger;
    
    public GridController(InterfazContactoSim ics, Logger logger) {
        this.ics = ics;
        this.logger = logger;
    }
    
    // 1. el endpoint que pide el profesor: texto plano tal cual el swagger
    @GetMapping(value = "/grid", produces = "text/plain")
    @ResponseBody 
    public String solicitudTexto(@RequestParam int tok) {
        DatosSimulation ds = ics.descargarDatos(tok);
        
        StringBuilder sb = new StringBuilder();
        // primera línea: el ancho
        sb.append(ds.getAnchoTablero()).append("\n");
        
        // resto de líneas: tiempo,y,x,color
        if (ds.getPuntos() != null) {
            ds.getPuntos().forEach((tiempo, lista) -> {
                for (Punto p : lista) {
                    sb.append(tiempo).append(",")
                      .append(p.getY()).append(",")
                      .append(p.getX()).append(",")
                      .append(p.getColor()).append("\n");
                }
            });
        }
        return sb.toString();
    }

    // 2. el endpoint extra por si quieres ver la matriz de colores (el grid.html)
    @GetMapping("/grid-visual")
    public String solicitudVisual(@RequestParam int tok, Model model) {
        DatosSimulation ds = ics.descargarDatos(tok);
        model.addAttribute("count", ds.getAnchoTablero());
        model.addAttribute("maxTime", ds.getMaxSegundos() > 0 ? ds.getMaxSegundos() - 1 : 0);
        
        Map<String, String> colorsMap = new HashMap<>();
        if (ds.getPuntos() != null) {
            ds.getPuntos().forEach((t, lista) -> {
                for (Punto p : lista) {
                    colorsMap.put(t + "-" + p.getY() + "-" + p.getX(), p.getColor());
                }
            });
        }
        model.addAttribute("colors", colorsMap);
        return "grid";
    }
}