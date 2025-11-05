package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.ProductStatsDTO;
import org.tung.healthycheck.model.Product;
import org.tung.healthycheck.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product getProductById(Long id) {return productRepository.findById(id).orElseThrow(()-> new RuntimeException("Not Found!"));}
    public Product saveProduct(Product product) {return productRepository.save(product);}
    public ProductStatsDTO getProductStats() {
        long totalCount = productRepository.count();
        double totalPrice = productRepository.getTotalPrice();
        return new ProductStatsDTO(totalCount, totalPrice);
    }
}