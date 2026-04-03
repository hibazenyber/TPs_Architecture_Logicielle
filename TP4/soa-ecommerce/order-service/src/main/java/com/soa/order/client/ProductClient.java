package com.soa.order.client;

import com.soa.order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Client Feign pour communiquer avec le product-service.
 * Le nom "product-service" correspond au spring.application.name du product-service,
 * Eureka se charge de resoudre l'URL reelle.
 */
@FeignClient(name = "product-service")
public interface ProductClient {

    /**
     * Recupere un produit par son ID depuis product-service.
     * Leve FeignException.NotFound (404) si le produit n'existe pas.
     */
    @GetMapping("/api/products/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id);

    /**
     * Met a jour le stock d'un produit (Exercice 1 & 2).
     * Appele apres creation d'une commande pour decrementer le stock.
     */
    @PutMapping("/api/products/{id}/stock")
    ResponseEntity<ProductDTO> updateStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity);
}
