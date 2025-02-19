package com.example.jew_backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Entity
@Table(name = "gold_price")
public class GoldPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "price_buy", nullable = false)
    @JsonProperty("price_buy")
    private BigDecimal priceBuy;

    @Column(name = "price_sold", nullable = false)
    @JsonProperty("price_sold")
    private BigDecimal priceSold;

    public GoldPrice() {
    }

    public GoldPrice(String name, BigDecimal priceBuy, BigDecimal priceSold) {
        this.name = name;
        this.priceBuy = priceBuy;
        this.priceSold = priceSold;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(BigDecimal priceBuy) {
        this.priceBuy = priceBuy;
    }

    public BigDecimal getPriceSold() {
        return priceSold;
    }

    public void setPriceSold(BigDecimal priceSold) {
        this.priceSold = priceSold;
    }

    @Override
    public String toString() {
        return "GoldPrice{id=" + id + ", name='" + name + "', price_buy=" + priceBuy + ", price_sold=" + priceSold + "}";
    }
}
