package com.shop.dto;

import com.shop.domain.Money;

public record MoneyDto(int amount, String currency) {
  public static MoneyDto from(Money money) {
    return new MoneyDto(money.amount(), money.currency());
  }
}
