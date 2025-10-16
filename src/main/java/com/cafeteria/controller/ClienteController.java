package com.cafeteria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafeteria.model.Cliente;
import com.cafeteria.repository.ClienteRepository;


@Controller
@RequestMapping("/clientes")
public class ClienteController{
	@Autowired
	private ClienteRepository repositoryCliente;
	@Autowired 
	private TipoDocRepository repositoryTipoDoc;
	@GetMapping
	public String ListarCliente(Model model) {
		List<Cliente> clientes= repositoryCliente.findAll();
		model.addAttribute("clientes",clientes);
		System.out.println("========== CLIENTES ENCONTRADOS ==========");
		System.out.println(clientes);
		System.out.println("==========================================");
		return "listarclientes";
	}
	
	@GetMapping("/nuevo")
	public String AgregarCliente(Model model) {
		model.addAttribute("cliente", new Cliente());
		List<TipoDoc> Tiposdoc=repositoryTipoDoc.findAll();
		model.addAttribute("tiposdoc", Tiposdoc);
		
		return "agregarclientes";
	}
	
	@PostMapping("/guardar")
	public String GuardarCliente(Cliente cliente, Model model) {
		repositoryCliente.save(cliente);
		List<Cliente> clientes= repositoryCliente.findAll();
		model.addAttribute("clientes",clientes);
		return "redirect:/clientes";
		 
		
	}
}