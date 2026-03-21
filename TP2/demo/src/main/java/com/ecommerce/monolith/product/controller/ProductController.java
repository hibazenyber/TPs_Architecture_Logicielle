package com.ecommerce.monolith.product.controller;

<<<<<<< HEAD
import com.ecommerce.monolith.product.model.Product;
import com.ecommerce.monolith.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
=======
import com.ecommerce.monolith.dto.ProductDto;
import com.ecommerce.monolith.product.service.ProductService;
import jakarta.validation.Valid; // Import important
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

>>>>>>> 38f38b4 (TP1: Persistance et API REST terminées et compilées avec succès)
import java.util.List;

@RestController
@RequestMapping("/api/products")
<<<<<<< HEAD
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = service.getAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = service.getById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product created = service.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @Valid @RequestBody Product product) {
        Product updated = service.update(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
=======
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        // Le @Valid vérifie les règles du DTO avant d'entrer ici
        return productService.saveProduct(productDto);
    }
}
>>>>>>> 38f38b4 (TP1: Persistance et API REST terminées et compilées avec succès)
