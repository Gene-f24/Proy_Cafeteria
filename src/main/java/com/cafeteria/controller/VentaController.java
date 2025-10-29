package com.cafeteria.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cafeteria.model.Categoria;
import com.cafeteria.model.DetalleVenta;
import com.cafeteria.model.Producto;
import com.cafeteria.model.Venta;
import com.cafeteria.repository.ICategoriaRepository;
import com.cafeteria.repository.IDetalleVentaRepository;
import com.cafeteria.repository.IProductoRepository;
import com.cafeteria.repository.IVentaRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private IVentaRepository repoVenta;

    @Autowired
    private IProductoRepository repoProd;

    @Autowired
    private IDetalleVentaRepository repoDet;
    
    @Autowired
    private ICategoriaRepository repoCat;

    //VENTA
    
    // LISTAR TODAS LAS VENTAS
    @GetMapping("/cargarventas")
    public String ListarVenta(Model model, HttpSession session) {
    
    	if (session.getAttribute("usuarioLogueado") == null) {
    	    return "redirect:/login";
    	}
        // Se envía a la vista la lista de todas las ventas
        model.addAttribute("lstVentas", repoVenta.findAll());
        return "listarventas"; // vista que muestra la lista
    }

    // FORMULARIO PARA CREAR NUEVA VENTA
    @GetMapping("/nuevo")
    public String AgregarVenta(Model model) {
        // Se envía un objeto venta vacío a la vista
        model.addAttribute("venta", new Venta());
        return "/vendedor/agregarventa"; // vista para agregar venta
    }


    // CREAR NUEVA VENTA
    @PostMapping("/nueva")
    public String crearVenta(@ModelAttribute Venta venta, Model model) {
        // Validar que el cliente tenga DNI
        if (venta.getNroDocCli() == null || venta.getNroDocCli().isEmpty()) {
            model.addAttribute("venta", venta);
            model.addAttribute("error", "Debe ingresar el DNI del cliente antes de registrar la venta.");
            return "/vendedor/agregarventa";
        }

        // Inicializar valores de la venta
        venta.setFecha(LocalDateTime.now());
        venta.setSubtotal(0.0);
        venta.setIgv(0.0);
        venta.setTotal(0.0);

        repoVenta.save(venta);

        // Redirige al detalle de la venta recién creada
        return "redirect:/ventas/detalle/" + venta.getIdVenta();
    }

 
    // ELIMINAR VENTA COMPLETA
    @GetMapping("/eliminar/{idVenta}")
    public String eliminarVenta(@PathVariable int idVenta) {
        repoVenta.deleteById(idVenta);
        return "redirect:/ventas/cargarventas";
    }

    
    
    //DETALLE

    // VER DETALLE DE UNA VENTA
    @GetMapping("/detalle/{idVenta}")
    public String verDetalleVenta(@PathVariable int idVenta, Model model) {
        Venta v = repoVenta.findById(idVenta).orElse(null);

        if (v == null) {
            // Si no existe la venta, redirigir a listado con mensaje
            model.addAttribute("mensaje", "La venta no existe.");
            return "redirect:/ventas/cargarventas";
        }

        List<DetalleVenta> detalles = repoDet.findByObjVenta_IdVenta(idVenta);
        model.addAttribute("venta", v);
        model.addAttribute("lstDetalles", detalles);
        model.addAttribute("nuevoDetalle", new DetalleVenta());
        model.addAttribute("lstProductos", repoProd.findAll());
        return "detalleventa";
    }


    // AGREGAR PRODUCTO AL DETALLE DE VENTA
    @PostMapping("/detalle/agregarproducto")
    @Transactional
    public String agregarDetalle(@ModelAttribute DetalleVenta detalle, Model model) {
        Producto prod = repoProd.findById(detalle.getId_prod()).orElse(null);
        if (prod == null) {
            model.addAttribute("error", "Producto no encontrado.");
            return "detalleventa";
        }

        // Validar stock antes de agregar
        if (detalle.getCantidad() > prod.getStk_prod()) {
            model.addAttribute("venta", repoVenta.findById(detalle.getIdVenta()).orElse(null));
            model.addAttribute("lstDetalles", repoDet.findByObjVenta_IdVenta(detalle.getIdVenta()));
            model.addAttribute("nuevoDetalle", detalle);
            model.addAttribute("lstProductos", repoProd.findAll());
            model.addAttribute("error", "No se puede agregar. Stock insuficiente. Disponible: " + prod.getStk_prod());
            return "detalleventa"; // Muestra mensaje en la misma vista
        }

        detalle.setPrecio_unitario(prod.getPre_prod());
        detalle.setSubtotal(detalle.getCantidad() * prod.getPre_prod());
        repoDet.save(detalle);

        // Actualizar stock
        prod.setStk_prod(prod.getStk_prod() - detalle.getCantidad());
        repoProd.save(prod);

        // Actualizar totales
        Venta venta = repoVenta.findById(detalle.getIdVenta()).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        double nuevoSubtotal = repoDet.findByObjVenta_IdVenta(venta.getIdVenta()).stream()
                                       .mapToDouble(DetalleVenta::getSubtotal).sum();
        venta.setSubtotal(nuevoSubtotal);
        venta.setIgv(nuevoSubtotal * 0.18);
        venta.setTotal(venta.getSubtotal() + venta.getIgv());
        repoVenta.save(venta);

        return "redirect:/ventas/detalle/" + detalle.getIdVenta();
    }


    // EDITAR DETALLE DE VENTA
    @GetMapping("/detalle/editar/{idDetalle}")
    public String editarDetalle(@PathVariable int idDetalle, Model model) {
        DetalleVenta detalle = repoDet.findById(idDetalle).orElse(null);

        // Enviar detalle y lista de productos a la vista
        model.addAttribute("detalle", detalle);
        model.addAttribute("lstProductos", repoProd.findAll());

        return "editardetalles"; 
    }

    @PostMapping("/detalle/actualizar")
    @Transactional
    public String actualizarDetalle(@ModelAttribute DetalleVenta detalle) {

        // Obtener detalle actual
        DetalleVenta d = repoDet.findById(detalle.getIdDetalle())
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        // Obtener producto seleccionado
        Producto prod = repoProd.findById(detalle.getId_prod())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Calcular diferencia de cantidad para ajustar stock
        int diferencia = detalle.getCantidad() - d.getCantidad();

        // Validar stock si se aumenta la cantidad
        if (diferencia > 0 && diferencia > prod.getStk_prod()) {
            throw new RuntimeException("No hay suficiente stock disponible. Stock actual: " + prod.getStk_prod());
        }

        // Ajustar stock
        prod.setStk_prod(prod.getStk_prod() - diferencia);
        repoProd.save(prod);

        // Actualizar datos del detalle
        d.setCantidad(detalle.getCantidad());
        d.setId_prod(detalle.getId_prod());
        d.setPrecio_unitario(prod.getPre_prod());
        d.setSubtotal(d.getCantidad() * prod.getPre_prod());
        repoDet.save(d);

        // Recalcular totales de la venta
        actualizarTotales(d.getIdVenta());

        return "redirect:/ventas/detalle/" + d.getIdVenta();
    }

    // ELIMINAR DETALLE DE VENTA
    @GetMapping("/detalle/eliminar/{idDetalle}")
    @Transactional
    public String eliminarDetalle(@PathVariable int idDetalle) {
        // Obtener detalle
        DetalleVenta detalle = repoDet.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        // Devolver cantidad al stock del producto
        Producto prod = repoProd.findById(detalle.getId_prod())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        prod.setStk_prod(prod.getStk_prod() + detalle.getCantidad());
        repoProd.save(prod);

        // Guardar idVenta para redirigir
        int idVenta = detalle.getIdVenta();

        // Eliminar detalle
        repoDet.delete(detalle);

        // Actualizar totales de la venta
        actualizarTotales(idVenta);

        return "redirect:/ventas/detalle/" + idVenta;
    }
    
    // BUSCAR VENTAS POR FECHA
    @GetMapping("/buscar")
    public String buscarVentasPorFecha(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        List<Venta> ventas = repoVenta.findByFechaBetween(
                fecha.atStartOfDay(),
                fecha.atTime(23, 59, 59)
        );

        if (ventas.isEmpty()) {
            model.addAttribute("mensaje", "No existen ventas en la fecha seleccionada.");
        }

        model.addAttribute("lstVentas", ventas);
        model.addAttribute("fechaBuscada", fecha);

        return "listarventas";
    }

    
    // METODO AUXILIAR PARA ACTUALIZAR TOTALES
 
    private void actualizarTotales(int idVenta) {
        Venta venta = repoVenta.findById(idVenta).orElse(null);
        if (venta != null) {
            double nuevoSubtotal = repoDet.findByObjVenta_IdVenta(idVenta)
                                         .stream()
                                         .mapToDouble(DetalleVenta::getSubtotal)
                                         .sum();
            venta.setSubtotal(nuevoSubtotal);
            venta.setIgv(nuevoSubtotal * 0.18);
            venta.setTotal(nuevoSubtotal + venta.getIgv());
            repoVenta.save(venta);
        }
    }
    
    
    @Autowired
	private DataSource dataSource; // javax.sql

	@Autowired
	private ResourceLoader resourceLoader; // core.io
	
	
	// GRAFICO
	@GetMapping("/graficos")
	public void reportesGraficos(HttpServletResponse response) {
	    // opción 1
	    // response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:static/grafico2.jasper").getURI().getPath();
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//REPORTE CIRCULAR
	@GetMapping("/reportecircular")
	public void reportesCircular(HttpServletResponse response) {
	    // opción 1
	    // response.setHeader("Content-Disposition", "attachment; filename=\"reporte.pdf\";");
	    // opción 2
	    response.setHeader("Content-Disposition", "inline;");
	    
	    response.setContentType("application/pdf");
	    try {
	        String ru = resourceLoader.getResource("classpath:static/reporte2.jasper").getURI().getPath();
	        JasperPrint jasperPrint = JasperFillManager.fillReport(ru, null, dataSource.getConnection());
	        OutputStream outStream = response.getOutputStream();
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// FILTRO
	@GetMapping("/filtro")
	public String mostrarFiltro(Model model) {
	    model.addAttribute("producto", new Producto());
	    model.addAttribute("lstCategorias", repoCat.findAll());
	    return "filtroReporte"; 
	}
	
	@PostMapping("/grabarfiltro")
	public void grabarFiltro(@RequestParam String categoria,
	                         HttpServletResponse response) {
	    response.setHeader("Content-Disposition", "inline;");
	    response.setContentType("application/pdf");

	    try {
	        // Cargar el .jasper desde resources
	        Resource resource = resourceLoader.getResource("classpath:static/filtro.jasper");

	        if (!resource.exists()) {
	            throw new FileNotFoundException("El archivo Jasper no se encontró en resources: filtro.jasper");
	        }

	        // Obtener el InputStream del reporte
	        InputStream jasperStream = resource.getInputStream();

	        // Parámetros que se enviarán al reporte
	        Map<String, Object> params = new HashMap<>();
	        params.put("categoria", categoria); // Debe coincidir con el nombre del parámetro en Jasper

	        // Conexión a la base de datos
	        try (Connection con = dataSource.getConnection()) {
	            // Llenar el reporte con los datos
	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, con);

	            // Exportar el PDF directamente al navegador
	            try (OutputStream outStream = response.getOutputStream()) {
	                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	                outStream.flush();
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            response.setContentType("text/plain");
	            response.getWriter().write("Error al generar el reporte: " + e.getMessage());
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	    }
	}


}

	

