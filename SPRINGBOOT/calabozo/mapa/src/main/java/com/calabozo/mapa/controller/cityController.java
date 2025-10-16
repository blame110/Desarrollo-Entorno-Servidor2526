package com.calabozo.mapa.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.calabozo.mapa.model.Ciudad;
import com.calabozo.mapa.repository.CiudadRepository;

@Controller
@RequestMapping("/ciudades")
public class cityController {

    // Creamos el loger
    private Logger logger = LoggerFactory.getLogger(cityController.class);

    @Autowired
    private CiudadRepository ciudadRepository;

    @GetMapping
    public String listCities(Model model) {

        // Cargamos todas las ciudades de BD
        List<Ciudad> listaCiudades = ciudadRepository.findAll();

        // Cargamos en model los datos de la lista de ciudades para que
        // le llegue a la pagina web destino (vista)
        model.addAttribute("ciudades", listaCiudades);

        return "listaCiudades";

    }

    @GetMapping("/eliminar/{id}")
    public String removeCity(@PathVariable Long id, RedirectAttributes redAttrib) {

        if (!ciudadRepository.existsById(id))
            redAttrib.addFlashAttribute("error", "La ciudad no Existe");
        else {
            ciudadRepository.deleteById(id);
            redAttrib.addFlashAttribute("success", "Se ha borrado Correctamente la ciudad con id " + id);
            logger.info("Se ha borrado Correctamente la ciudad con id " + id);
        }

        return "redirect:/ciudades";
    }

    @GetMapping("/nuevo")
    public String newCity(Model model) {

        // Creamos una ciudad para que el formulario
        // la asocie a sus datos
        Ciudad ciudad = new Ciudad();
        // La guardamos en el model para que le llegue al formulario
        model.addAttribute("ciudad", ciudad);
        // Cargamos la vista nuevaCiudad
        return "nuevaCiudad";
    }

    @PostMapping("/crear")
    public String createCity(@ModelAttribute("ciudad") Ciudad ciudad) {

        ciudadRepository.save(ciudad);

        logger.info("Se ha creado la ciudad con id " + ciudad.getId());

        return "redirect:/ciudades";
    }

    @GetMapping("/editar/{id}")
    public String editCity(@PathVariable Long id, Model model) {

        // Primero creo una ciudad en blanco
        Ciudad ciudad = new Ciudad();

        // Si no existe la ciudad con id en la bd devuelo el error
        if (!ciudadRepository.existsById(id))
            model.addAttribute("error", "La ciudad no Existe");
        // Si existe la ciudad en bd la cargamos
        else
            ciudad = ciudadRepository.findById(id).get();
        // Cargamos la ciudad en el model y cargamos la vista
        model.addAttribute("ciudad", ciudad);

        return "editarCiudad";
    }

    @PostMapping("/modificar")
    public String modifyCity(@ModelAttribute("ciudad") Ciudad ciudad, Model model) {

        try {
            // Guardamos la ciudad
            ciudadRepository.save(ciudad);

        } catch (Exception e) {
            model.addAttribute("error", "La ciudad no Existe");
            return "redirect:/ciudades";
        }

        return "redirect:/ciudades";

    }

}
