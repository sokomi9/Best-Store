package com.sokomi.app.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sokomi.app.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

}
