package com.cafeteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {
	 @GetMapping("/logout")
	    public String cerrarSesion(HttpSession session) {
	        session.invalidate(); 
	        return "redirect:/login";
	    }
}
