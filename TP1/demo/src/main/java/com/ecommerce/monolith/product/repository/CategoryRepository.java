package com.ecommerce.monolith.product.repository;

import com.ecommerce.monolith.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
