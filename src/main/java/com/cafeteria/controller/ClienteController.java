package com.cafeteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cafeteria.model.Cliente;
import com.cafeteria.model.Usuario;
import com.cafeteria.repository.IClienteRepository;
import com.cafeteria.repository.ITipoDocRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/clientes") //ruta
public class ClienteController{
	//llama al repositorio
	@Autowired //por cada repository
	private IClienteRepository repoCli;
	@Autowired //por cada repository
	private ITipoDocRepository repoTipoDoc;
	
	//abrir la pagina listar cliente
	@GetMapping("/cargarclientes")//endpoint
	public String ListarCliente(Model model) {
		//enviar el listado de los Clientes
		model.addAttribute("lstClientes",repoCli.findAll());
		return "listarclientes"; // nombre del recurso 
	}
	
	@GetMapping("/nuevo")
	public String AgregarCliente(Model model) {
		//enviar el listado de categoria
		model.addAttribute("lstTipodoc", repoTipoDoc.findAll());
		//enviar un Cliente vacio al formulario
		model.addAttribute("cliente", new Cliente());
		
		return "agregarclientes";
	}
	
	//Seleccionar el Cliente a Editar, mediante un href
		@GetMapping("/editar/{id_cli}")
		public String editarCliente(@PathVariable Integer id_cli, Model model) {
			//obtener un Cliente segun el  id
			Cliente c = repoCli.findById(id_cli).get();
			//Enviar el Cliente al formulario (como atributo)
			model.addAttribute("cliente",c);
			model.addAttribute("lstTipodoc", repoTipoDoc.findAll());
			return "editarclientes";
		}
	//Guardar los cambios
	@PostMapping("/guardar")
	public String guardarCliente(@ModelAttribute Cliente cliente, Model model) { 
		try {
			System.out.println("=== Datos recibidos del formulario ===");
	        System.out.println(cliente);
			model.addAttribute("mensaje", "Cliente guardado correctamente.");
			repoCli.save(cliente);
			return "redirect:/clientes/cargarclientes";
		}catch(Exception e) {
			model.addAttribute("mensaje", "Error al guardar el cliente: " + e.getMessage());
			e.printStackTrace(); // para ver el error completo en consola
			return "listarclientes";
		}
	}
	
	//Eliminar Clientes
	@GetMapping("/eliminar/{id_cli}")
	public String EliminarCliente(@PathVariable Integer id_cli) {
		repoCli.deleteById(id_cli);
				
		return "redirect:/clientes/cargarclientes";
	}
	
	//Buscar cliente por numero de documento
	@GetMapping("/buscar")
    public String buscarCliente(@RequestParam("txtNroDocCli") String nroDocCli, Model model) {
		Cliente cliente = repoCli.findByNroDocCli(nroDocCli);
        if (cliente != null) {
            model.addAttribute("lstClientes", java.util.List.of(cliente));
        } else {
            model.addAttribute("mensaje", "No se encontró ningún cliente con ese número de documento.");
        }

        return "listarclientes";
    }
}