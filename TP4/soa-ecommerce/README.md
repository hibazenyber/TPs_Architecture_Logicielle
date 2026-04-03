# TP SOA E-Commerce — Spring Boot Microservices

Architecture Orientée Services avec 3 microservices Spring Boot communiquant via Eureka et OpenFeign.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Eureka Server :8761                    │
│                  (Service de découverte)                    │
└──────────────────────┬──────────────────┬───────────────────┘
                       │  s'enregistrent  │
          ┌────────────▼──────┐  ┌────────▼───────────┐
          │  product-service  │  │   order-service    │
          │     :8081         │◄─┤     :8082          │
          │  (Gestion produits│  │ (Gestion commandes)│
          │   H2: productdb)  │  │  H2: orderdb)      │
          └───────────────────┘  └────────────────────┘
                                   Feign Client ──────►
```

## Prérequis

- Java 17+
- Maven 3.8+

## Démarrage (dans cet ordre !)

### 1. Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Vérification → http://localhost:8761 (dashboard Eureka)

### 2. Product Service

```bash
cd product-service
mvn spring-boot:run
```

- API : http://localhost:8081/api/products
- H2 Console : http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:mem:productdb`)

### 3. Order Service

```bash
cd order-service
mvn spring-boot:run
```

- API : http://localhost:8082/api/orders

> Attendre que product-service soit enregistré dans Eureka avant de tester order-service.

---

## Endpoints

### product-service (port 8081)

| Méthode | URL                             | Description                     |
|---------|---------------------------------|---------------------------------|
| GET     | `/api/products`                 | Liste tous les produits         |
| GET     | `/api/products/{id}`            | Récupère un produit par ID      |
| POST    | `/api/products`                 | Crée un produit (body JSON)     |
| DELETE  | `/api/products/{id}`            | Supprime un produit             |
| PUT     | `/api/products/{id}/stock?stock=N` | Met à jour le stock (Exercice 1) |

### order-service (port 8082)

| Méthode | URL                                                  | Description          |
|---------|------------------------------------------------------|----------------------|
| GET     | `/api/orders`                                        | Liste les commandes  |
| POST    | `/api/orders?productId={id}&quantity={n}`            | Crée une commande    |

---

## Requêtes Postman / curl

### 1. Créer un produit

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS",
    "description": "Laptop 15 pouces, 16Go RAM, 512Go SSD",
    "price": 1299.99,
    "stock": 10
  }'
```

Réponse attendue :
```json
{
  "id": 1,
  "name": "Laptop Dell XPS",
  "description": "Laptop 15 pouces, 16Go RAM, 512Go SSD",
  "price": 1299.99,
  "stock": 10
}
```

### 2. Créer un second produit

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Souris Logitech MX",
    "description": "Souris sans fil ergonomique",
    "price": 79.90,
    "stock": 50
  }'
```

### 3. Lister tous les produits

```bash
curl http://localhost:8081/api/products
```

### 4. Récupérer un produit par ID

```bash
curl http://localhost:8081/api/products/1
```

### 5. Mettre à jour le stock (Exercice 1)

```bash
curl -X PUT "http://localhost:8081/api/products/1/stock?stock=5"
```

### 6. Créer une commande (Exercice 2 & 3)

```bash
curl -X POST "http://localhost:8082/api/orders?productId=1&quantity=2"
```

Réponse attendue :
```json
{
  "id": 1,
  "productId": 1,
  "productName": "Laptop Dell XPS",
  "quantity": 2,
  "totalPrice": 2599.98,
  "orderDate": "2026-03-30T10:00:00",
  "status": "PENDING"
}
```

Le stock du produit 1 est automatiquement décrémenté (10 → 8).

### 7. Tester le stock insuffisant (Exercice 2)

```bash
# Commande de 100 unités alors que le stock est insuffisant
curl -X POST "http://localhost:8082/api/orders?productId=1&quantity=100"
```

Réponse (HTTP 400) :
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Stock insuffisant pour 'Laptop Dell XPS'. Disponible : 8, demandé : 100"
}
```

### 8. Tester produit inexistant (Exercice 3)

```bash
curl -X POST "http://localhost:8082/api/orders?productId=999&quantity=1"
```

Réponse (HTTP 400) :
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Produit introuvable avec l'ID : 999"
}
```

### 9. Lister toutes les commandes

```bash
curl http://localhost:8082/api/orders
```

### 10. Supprimer un produit

```bash
curl -X DELETE http://localhost:8081/api/products/2
```

---

## Exercices implémentés

### Exercice 1 — Mise à jour du stock
- `PUT /api/products/{id}/stock?stock=N` dans `ProductController`
- Logique dans `ProductService.updateStock()`

### Exercice 2 — Décrémentation du stock à la commande
- `OrderService.createOrder()` appelle `productClient.updateStock()` après validation
- Si le stock est insuffisant → exception avec message explicite

### Exercice 3 — Gestion d'erreurs Feign
- `FeignException.NotFound` → message "Produit introuvable"
- `FeignException` générale → message "service indisponible"
- `GlobalExceptionHandler` centralise la réponse HTTP 400 structurée

---

## Structure du projet

```
soa-ecommerce/
├── eureka-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/soa/eureka/EurekaServerApplication.java
│       └── resources/application.properties
│
├── product-service/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/soa/product/
│       │   ├── ProductServiceApplication.java
│       │   ├── entity/Product.java
│       │   ├── repository/ProductRepository.java
│       │   ├── service/ProductService.java
│       │   └── controller/ProductController.java
│       └── resources/application.properties
│
└── order-service/
    ├── pom.xml
    └── src/main/
        ├── java/com/soa/order/
        │   ├── OrderServiceApplication.java
        │   ├── entity/Order.java
        │   ├── dto/ProductDTO.java
        │   ├── client/ProductClient.java
        │   ├── repository/OrderRepository.java
        │   ├── service/OrderService.java
        │   ├── controller/OrderController.java
        │   └── exception/GlobalExceptionHandler.java
        └── resources/application.properties
```
