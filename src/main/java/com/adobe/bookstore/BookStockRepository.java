package com.adobe.bookstore;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookStockRepository extends JpaRepository<BookStock, String> {

    Optional < BookStock > findByName(String id);
    Optional < BookStock > findById(String id);

}
