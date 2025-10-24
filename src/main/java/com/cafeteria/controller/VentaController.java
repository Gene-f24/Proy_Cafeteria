package com.cafeteria.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cafeteria.model.Cliente;
import com.cafeteria.model.DetalleVenta;
import com.cafeteria.model.Producto;
import com.cafeteria.model.Venta;
import com.cafeteria.repository.IClienteRepository;
import com.cafeteria.repository.IDetalleVentaRepository;
import com.cafeteria.repository.IProductoRepository;
import com.cafeteria.repository.IVentaRepository;

@Controller
@RequestMapping("/ventas")
public class VentaController {
	
	//llama al repositorio
	@Autowired
	private IVentaRepository repoVenta;
	@Autowired
	private IClienteRepository repoCli;
	@Autowired
	private IProductoRepository repoProd;
	@Autowired
	private IDetalleVentaRepository repoDet;
	
	// ENCABEZADO
	//Listar Venta
	@GetMapping("/cargarventas") //endpoint
	public String ListarVenta(Model model) {
		//enviar el listado de Ventas
		model.addAttribute( "lstVentas", repoVenta.findAll());
		return "listarventas"; // nombre del recurso 
	}
	
	// Mostrar formulario para crear nueva venta
	@GetMapping("/nuevo")
	public String AgregarVenta(Model model) {
		//enviar una venta vacia (revisar com)
		model.addAttribute("venta", new Venta());
		// enviar el listado de clientes
		model.addAttribute("lstClientes", repoCli.findAll());
		return "agregarventa";// nombre del recurso
	}

	 
	// Crear nueva venta vacia (solo encabezado)
	@PostMapping("/nueva")
	public String crearVenta(@ModelAttribute Venta venta) {
		venta.setFecha(LocalDate.now().toString());
		venta.setSubtotal(0.0);
		venta.setIgv(0.0);
		venta.setTotal(0.0);
		repoVenta.save(venta);
		// Redirige al detalle de la nueva venta
		return "redirect:/ventas/detalle/" + venta.getIdVenta();
	}
	
	
	//Eliminar Venta
	@GetMapping("/eliminar/{idVenta}")
	public String eliminarVenta(@PathVariable int idVenta) {
	    repoVenta.deleteById(idVenta);
	    return "redirect:/ventas/cargarventas";
	}
	
	//DETALLE VENTA
	//Ver detalle de una venta especifica
    @GetMapping("/detalle/{idVenta}")
    public String verDetalleVenta(@PathVariable int idVenta, Model model) {
        // obtener la venta
        Venta v = repoVenta.findById(idVenta).orElse(null);
        // obtener los detalles de esa venta
        List<DetalleVenta> detalles = repoDet.findByObjVenta_IdVenta(idVenta);
        // enviar a la vista
        model.addAttribute("venta", v);
        model.addAttribute("lstDetalles",detalles);
        model.addAttribute("nuevoDetalle",new DetalleVenta());
        model.addAttribute("lstProductos", repoProd.findAll());

        return "detalleventa"; 
    }


    // Agregar un producto a la venta
	@PostMapping("/detalle/agregarproducto")
	public String agregarDetalle(@ModelAttribute DetalleVenta detalle) {

		// Buscar el producto usando su ID
		Producto prod = repoProd.findById(detalle.getId_prod()).orElse(null);

		if (prod != null) {
			// Asignar precio y calcular subtotal
			detalle.setPrecio_unitario(prod.getPre_prod());
			detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecio_unitario());
		} else {
			// Si no existe el producto, redirige con mensaje de error o maneja la excepción
			return "redirect:/ventas/detalle/" + detalle.getIdVenta() + "?error=productoNoEncontrado";
		}

		// Guardar el detalle en la base de datos
		repoDet.save(detalle);

		// Buscar la venta asociada
		Venta venta = repoVenta.findById(detalle.getIdVenta()).orElse(null);

		if (venta != null) {
			// Obtener todos los detalles de esa venta
			List<DetalleVenta> detalles = repoDet.findByObjVenta_IdVenta(venta.getIdVenta());

			// Calcular nuevos totales
			double nuevoSubtotal = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();

			double nuevoIgv = nuevoSubtotal * 0.18;
			double nuevoTotal = nuevoSubtotal + nuevoIgv;

			// Actualizar los campos de la venta
			venta.setSubtotal(nuevoSubtotal);
			venta.setIgv(nuevoIgv);
			venta.setTotal(nuevoTotal);

			// Guardar cambios
			repoVenta.save(venta);
		}

		// Redirigir al detalle de la venta
		return "redirect:/ventas/detalle/" + detalle.getIdVenta();
	}

    
    // Eliminar producto del detalle
    @GetMapping("/detalle/eliminar/{idDetalle}")
    public String eliminarDetalle(@PathVariable int idDetalle) {
        DetalleVenta detalle = repoDet.findById(idDetalle).orElse(null);
        if (detalle != null) {
            int idVenta = detalle.getObjVenta().getIdVenta();
            repoDet.delete(detalle);

            // recalcular totales
            List<DetalleVenta> detalles = repoDet.findByObjVenta_IdVenta(idVenta);
            Venta venta = repoVenta.findById(idVenta).orElse(null);
            if (venta != null) {
                double nuevoSubtotal = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
                double nuevoIgv = nuevoSubtotal * 0.18;
                double nuevoTotal = nuevoSubtotal + nuevoIgv;
                venta.setSubtotal(nuevoSubtotal);
                venta.setIgv(nuevoIgv);
                venta.setTotal(nuevoTotal);
                repoVenta.save(venta);
            }
            return "redirect:/ventas/detalle/" + idVenta;
        }
        return "redirect:/ventas/cargarventas";
    }	
}
