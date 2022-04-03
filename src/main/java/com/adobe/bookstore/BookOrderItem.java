package com.adobe.bookstore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "book_order_item")
@JsonSerialize
public class BookOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_order_id", nullable = false)
    private BookOrder bookOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_stock_id", nullable = false) //Assuming it acts like Book class
    private BookStock bookStock;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    public BookOrderItem(){
    }

    public BookOrderItem(BookOrder bookOrder, BookStock bookStock, Integer quantity){
        this.bookOrder = bookOrder;
        this.bookStock = bookStock;
        this.quantity = quantity;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBookStock() {
        return bookStock.getId();
    }

    // public void setBookStock(BookStock bookStock) {
    //     this.bookStock = bookStock;
    // }

    @Override
    public String toString() {
        return "Item: {" +
                "id=" + id +
                //", order='" + bookOrder + '\'' +
                ", book=" + bookStock +
                ", quantity=" + quantity +
                '}';
    }

}
