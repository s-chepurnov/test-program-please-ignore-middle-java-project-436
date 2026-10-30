package com.shop.service;

import com.shop.domain.Promo;
import com.shop.repository.PromoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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
