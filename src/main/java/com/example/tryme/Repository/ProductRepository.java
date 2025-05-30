package com.example.tryme.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tryme.Model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
}