package com.soa.product.service;

import com.soa.product.entity.Product;
import com.soa.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Exercice 1 : reduire le stock d'un produit par une quantite donnee
    public Optional<Product> updateStock(Long id, Integer quantity) {
        Optional<Product> opt = productRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        Product product = opt.get();
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Stock insuffisant");
        }
        product.setStock(product.getStock() - quantity);
        return Optional.of(productRepository.save(product));
    }
}
