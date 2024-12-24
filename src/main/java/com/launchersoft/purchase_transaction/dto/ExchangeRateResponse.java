package com.launchersoft.purchase_transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExchangeRateResponse {
    // Getters e Setters
    private String currency;
    private LocalDate date;
    private BigDecimal rate;

}
