package com.cafeteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {
	    @GetMapping("/login")
	    public String mostrarLogin() {
	        return "login"; // busca en templates/login.html
	    }
	    @PostMapping("/login")
	    public String procesarLogin(@RequestParam(name="usuario") String usuario, 
	                                @RequestParam(name="clave") String clave, 
	                                Model model) {
	        if (usuario.equals("admin") && clave.equals("1234")) {
	            return "redirect:/home";
	        } else {
	            model.addAttribute("error", "Usuario o contraseña incorrectos");
	            return "login";
	        }
	    }
	

}
