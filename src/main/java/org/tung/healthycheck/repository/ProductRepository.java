package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tung.healthycheck.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
//    gắn trực tiếp vào phương thức interface để chạy câu truy vấn
    @Query("SELECT COALESCE(SUM(p.price), 0) FROM Product p")
    double getTotalPrice();

    @Override
    long count();

}
