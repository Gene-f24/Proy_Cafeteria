package com.cafeteria.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cafeteria.model.Producto;
import com.cafeteria.model.Usuario;
import com.cafeteria.repository.ICategoriaRepository;
import com.cafeteria.repository.IProductoRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/productos") //ruta
public class ProductoController {
	//llama al repositorio
	@Autowired //por cada repository
	private ICategoriaRepository repoCat;
	
	@Autowired //por cada repository
	private IProductoRepository repoProd;
	
	//abrir la pagina listar producto
	@GetMapping("/cargarproductos")//endpoint
	
	public String ListarProducto(Model model) {
		//enviar el listado de los Productos
		model.addAttribute("lstProductos", repoProd.findAll());
		return "listarproductos"; // nombre del recurso 
	}
	
	//Agregar Nuevo Producto
	@GetMapping("/nuevo")
	public String AgregarProducto(Model model) {
		//enviar el listado de categoria
		model.addAttribute("lstCategorias",repoCat.findAll());
		//enviar un Producto vacio al formulario
		model.addAttribute("producto", new Producto());
		return "agregarproductos";// nombre del recurso 
	}
	
	//Seleccionar el Producto a Editar, mediante un href
	@GetMapping("/editar/{id_prod}")
	public String editarProducto(@PathVariable Integer id_prod, Model model) {
		//obtener un Producto segun el  id
		Producto p = repoProd.findById(id_prod).get();
		//Enviar el Producto al formulario (como atributo)
		model.addAttribute("producto",p);
		model.addAttribute("lstCategorias", repoCat.findAll());
		return "editarproductos";
	}
	
	
	//Eliminar Producto
	@GetMapping("/eliminar/{id_prod}")
	public String EliminarCliente(@PathVariable Integer id_prod) {
		repoProd.deleteById(id_prod);
		
		return "redirect:/productos/cargarproductos";
	}
	
	// Buscar por descripción o categoría
	@GetMapping("/buscar")
	public String buscarProductos(@RequestParam("txtProducto") String descripcion, Model model,HttpSession session) {
		 Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
    	 model.addAttribute("usuario", u); 
    	 
		List<Producto> productos = repoProd.findByDesProdContainingIgnoreCaseOrObjCat_DesCategContainingIgnoreCase(descripcion, descripcion);
	    model.addAttribute("lstProductos", productos);
	    model.addAttribute("descripcion", descripcion);
	    if (productos.isEmpty()) {
	        model.addAttribute("mensaje", "No se encontraron productos.");
	    }
	    return "listarproductos";
	}
	
	//Guardar los cambios
	@PostMapping("/guardar")
	public String guardarProducto(@ModelAttribute Producto producto, Model model) {
		try {
			repoProd.save(producto);
			return "redirect:/productos/cargarproductos";
		}catch(Exception e) {
			model.addAttribute("mensaje", "Error al guardar el producto: " + e.getMessage());
			e.printStackTrace(); // para ver el error completo en consola
			return "listarproductos";
		}
	}
	
}
