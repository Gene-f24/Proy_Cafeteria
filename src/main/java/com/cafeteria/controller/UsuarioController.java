package com.cafeteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafeteria.repository.IUsuarioRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/usuarios") //ruta
public class UsuarioController {
	//llama al repositorio
		@Autowired //por cada repository
		private IUsuarioRepository repoUsua;

		//abrir la pagina listar cliente
		@GetMapping("/cargarusuarios")//endpoint
		public String ListarUsuario(Model model) {
			//enviar el listado de los Usuarios
			model.addAttribute("lstUsuarios",repoUsua.findAll());
			return "administrador/listarusuarios"; // nombre del recurso 
		}
		
		//Eliminar Usuarios
		@Transactional
		@GetMapping("/eliminar/{idusua}")
		public String EliminarUsuario(@PathVariable Integer idusua) {
			repoUsua.deleteByIdusua(idusua);	
			return "redirect:/usuarios/cargarusuarios";
		}
		
}
