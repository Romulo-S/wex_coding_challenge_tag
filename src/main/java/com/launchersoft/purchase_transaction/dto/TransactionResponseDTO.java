package com.launchersoft.purchase_transaction.dto;

import com.launchersoft.purchase_transaction.entity.PurchaseTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Currency;

@Data
public class TransactionResponseDTO {
    private Long id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount; // Valor original (USD)
    private BigDecimal exchangeRate;   // Taxa de câmbio
    private String formattedConvertedAmount; // Campo único para o valor formatado

    public TransactionResponseDTO(PurchaseTransaction transaction, BigDecimal convertedAmount, String currencyCode, BigDecimal exchangeRate) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.transactionDate = LocalDate.parse(transaction.getTransactionDate().toString());
        this.originalAmount = transaction.getAmount();
        this.exchangeRate = exchangeRate;

        // Formata o valor de acordo com a moeda fornecida
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        try {
            Currency currency = Currency.getInstance(currencyCode);
            currencyFormat.setCurrency(currency);
        } catch (IllegalArgumentException e) {
            // Caso o código da moeda seja inválido, usamos o padrão (ex.: USD)
            currencyFormat.setCurrency(Currency.getInstance("USD"));
        }

        this.formattedConvertedAmount = currencyFormat.format(convertedAmount);
    }

}
