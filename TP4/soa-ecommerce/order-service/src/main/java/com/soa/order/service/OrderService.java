package com.soa.order.service;

import com.soa.order.client.ProductClient;
import com.soa.order.dto.ProductDTO;
import com.soa.order.entity.Order;
import com.soa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    /**
     * Retourne toutes les commandes.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Cree une commande :
     * 1. Recupere le produit via Feign (gestion 404 et service indisponible)
     * 2. Verifie que le stock est suffisant
     * 3. Met a jour le stock dans product-service
     * 4. Persiste la commande avec status=PENDING
     *
     * Exercices 2 et 3 implementes ici.
     */
    public Order createOrder(Long productId, Integer quantity) {

        // --- Exercice 3 : recuperer le produit (FeignException remonte au GlobalExceptionHandler) ---
        ResponseEntity<ProductDTO> response = productClient.getProductById(productId);
        ProductDTO product = response.getBody();
        if (product == null) {
            throw new RuntimeException("Produit introuvable avec l'ID : " + productId);
        }

        // --- Exercice 1 : verifier le stock ---
        if (product.getStock() < quantity) {
            throw new RuntimeException(
                    String.format("Stock insuffisant pour '%s'. Disponible : %d, demande : %d",
                            product.getName(), product.getStock(), quantity));
        }

        // --- Exercice 1 : decrementer le stock dans product-service ---
        productClient.updateStock(productId, quantity);
        log.info("Stock du produit {} decremente de {}", productId, quantity);

        // --- Creer et sauvegarder la commande ---
        Order order = Order.builder()
                .productId(productId)
                .productName(product.getName())
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity)
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .build();

        Order saved = orderRepository.save(order);
        log.info("Commande {} creee avec succes pour le produit '{}'", saved.getId(), product.getName());
        return saved;
    }
}
