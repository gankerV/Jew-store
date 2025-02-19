package com.example.jew_backend.service;

import com.example.jew_backend.model.GoldPrice;
import com.example.jew_backend.repository.GoldPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoldPriceService {

    @Autowired
    private GoldPriceRepository goldPriceRepository;

    public List<GoldPrice> getAllGoldPrices() {
        return goldPriceRepository.findAll();
    }

    public void updateAllGoldPrices(List<GoldPrice> updatedGoldPrices) {
        // Xoá tất cả các bản ghi trong bảng gold_price
        System.out.println("Danh sách giá vàng mới: ");
        updatedGoldPrices.forEach(goldPrice -> System.out.println(goldPrice));
        goldPriceRepository.deleteAll();

        // Đặt ID về null để Hibernate biết đây là bản ghi mới
        updatedGoldPrices.forEach(goldPrice -> goldPrice.setId(null));

        // Lưu lại toàn bộ danh sách mới
        goldPriceRepository.saveAll(updatedGoldPrices);
    }
}
