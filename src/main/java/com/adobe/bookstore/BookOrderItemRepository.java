package com.adobe.bookstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookOrderItemRepository extends JpaRepository<BookOrderItem, Long> {
}
