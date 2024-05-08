package com.sokomi.app.controllers;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.sql.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.sokomi.app.models.Product;
import com.sokomi.app.models.ProductDto;
import com.sokomi.app.services.ProductRepository;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Controller
@RequestMapping("/products")
public class ProductsControllers {
	@Autowired
	private ProductRepository repo;
	@GetMapping({"", "/"})
	public String showProductList(Model model) {
		List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("products", products);
		return "products/index";
	 }
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}
		@PostMapping("/create")
		public String createProduct(
			@Valid @ModelAttribute ProductDto productDto,
				BindingResult result) {
	  	
			
			if (productDto.getImageFile().isEmpty()) {
				result.addError(new FieldError("productDto", "imageFile", "The image is required"));
			}
			if (result.hasErrors()) {
				return "products/CreateProduct";
			}
			
			MultipartFile image = productDto.getImageFile();
			Date createdAt = new Date();
			String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
			
			try {
				String uploadDir = "public/images/";
				Path uploadPath = Paths.get(uploadDir);
			
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				try(InputStream inputStream = image.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
			} 
			
			Product product = new Product();
			product.setName(productDto.getName());
			product.setBrand(productDto.getBrand());
			product.setCategory(productDto.getCategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());
			product.setCreatedAt(createdAt);
			product.setImageFileName(storageFileName);
			
				repo.save(product);
			return "redirect:/products";
	
	}
}
