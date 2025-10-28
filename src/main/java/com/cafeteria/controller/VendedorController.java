package com.cafeteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vend")
public class VendedorController {
	@GetMapping("/home")
    public String homeVendedor() {
        return "vendedor/home"; 
    }
}
