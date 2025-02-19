package com.example.jew_backend.repository;

import com.example.jew_backend.model.GoldPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Long> {
}
