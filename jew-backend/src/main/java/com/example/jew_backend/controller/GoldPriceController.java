package com.example.jew_backend.controller;

import com.example.jew_backend.model.GoldPrice;
import com.example.jew_backend.service.GoldPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gold-prices")
public class GoldPriceController {

    @Autowired
    private GoldPriceService goldPriceService;

    // API lấy danh sách giá vàng
    @GetMapping
    public ResponseEntity<List<GoldPrice>> getAllGoldPrices() {

        return ResponseEntity.ok(goldPriceService.getAllGoldPrices());
    }

    // API cập nhật toàn bộ bảng giá vàng
    @PutMapping("/update")
    public ResponseEntity<String> updateAllGoldPrices(@RequestBody List<GoldPrice> updatedGoldPrices) {
        try {
            goldPriceService.updateAllGoldPrices(updatedGoldPrices);
            return ResponseEntity.ok("Cập nhật bảng giá vàng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Cập nhật bảng giá vàng không thành công");
        }
    }
}
