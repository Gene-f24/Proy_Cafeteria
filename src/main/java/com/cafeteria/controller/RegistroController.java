package com.cafeteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cafeteria.model.Usuario;
import com.cafeteria.repository.IUsuarioRepository;

@Controller
@RequestMapping("/registro")
public class RegistroController {
	@Autowired 
	private IUsuarioRepository repoUsu;   
	
	@GetMapping("/cargarregistro")
	    public String mostrarLogin(Model model) {
		   	model.addAttribute("usuarios", new Usuario());
	        return "registro"; // busca en templates/login.html
	    }
	   
	 //Guardar los cambios
		@PostMapping("/guardar")
		public String guardarUsuario(@ModelAttribute Usuario usuarios, Model model) {
			try {
				System.out.println("=== Datos recibidos del formulario ===");
		        System.out.println(usuarios);
				model.addAttribute("mensaje", "Usuario registrado correctamente.");
				repoUsu.save(usuarios);
				return "redirect:/login";
			}catch(Exception e) {
				model.addAttribute("mensaje", "Error al registrarse" + e.getMessage());
				model.addAttribute("cssmensaje", "alert alert-danger");
				e.printStackTrace(); // para ver el error completo en consola
				return "registro";
			}
		}
}
