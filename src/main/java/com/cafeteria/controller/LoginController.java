package com.cafeteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cafeteria.model.Usuario;
import com.cafeteria.repository.IUsuarioRepository;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/")
public class LoginController {
		@Autowired 
		private IUsuarioRepository repoUsu;
		
		@GetMapping("/")
	    public String redirigirLogin() {
	        return "redirect:/login"; // dirige al login
	    }
		
	    @GetMapping("/login")
	    public String mostrarLogin() {
	        return "login"; // muestra el login
	    }
	    
	    @PostMapping("/login")
	    public String validarLogin(@RequestParam("txtUsuario") String correo, 
	                               @RequestParam("txtClave") String clave, Model model, HttpSession session) {
	    	
	    	//Validar Usuario
	    	Usuario u = repoUsu.findByCorreoAndClave(correo, clave);
	        if(u != null) {//si existe
	        	// Guarda el usuario en la sesión
	            session.setAttribute("usuarioLogueado", u);
	            // segun el tipo de usuario guarda la URL y redirige 
	            if (u.getIdtipousua() == 1) {
	                return "redirect:/admin/home";
	            }else {
	                return  "redirect:/vend/home";
	            }
	        }else {
	        	model.addAttribute("mensaje", "usuario o clave incorrectos");
	        	model.addAttribute("cssmensaje", "alert alert-danger");
	        	return "login";
	        }
	    }
}
