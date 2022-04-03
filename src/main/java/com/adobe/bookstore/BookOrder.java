package com.adobe.bookstore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "book_order", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@JsonSerialize
public class BookOrder {

    @Id
    @GeneratedValue(generator = "UUID")
    @Type(type="uuid-char")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Column(name = "success", nullable=false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean success;

    @OneToMany(mappedBy = "bookOrder", fetch = FetchType.LAZY,
    cascade = CascadeType.ALL)
    private Set<BookOrderItem> bookOrderItems  = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<BookOrderItem> getBookOrderItems() {
        return bookOrderItems;
    }

    public void setStoryList(Set<BookOrderItem> bookOrderItems) {
        this.bookOrderItems = bookOrderItems;
    }
}
