package com.ecommerce.monolith.product.repository;

import com.ecommerce.monolith.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "categories")
=======

>>>>>>> 38f38b4 (TP1: Persistance et API REST terminées et compilées avec succès)
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
