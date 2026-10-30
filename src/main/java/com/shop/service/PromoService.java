package com.shop.service;

import com.shop.domain.Promo;
import com.shop.repository.PromoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromoService {

    private final PromoRepository promoRepository;

    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

    public List<Promo> findAll() {
        return promoRepository.findAll();
    }
}
