package com.tiwpr.rest.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {

	@GetMapping({ "/", "/index" })
	public String showMainPage(){
		return "STUDIA MAGISTERSKIE PROJEKT TIWPR, ARTUR BLOCH, 2020";
	}
}