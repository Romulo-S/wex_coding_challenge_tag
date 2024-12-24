package com.launchersoft.purchase_transaction.controller;

import com.launchersoft.purchase_transaction.dto.TransactionResponseDTO;
import com.launchersoft.purchase_transaction.entity.PurchaseTransaction;
import com.launchersoft.purchase_transaction.repository.PurchaseTransactionRepository;
import com.launchersoft.purchase_transaction.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class PurchaseTransactionController {

    private final PurchaseTransactionRepository repository;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public PurchaseTransactionController(PurchaseTransactionRepository repository, ExchangeRateService exchangeRateService) {
        this.repository = repository;
        this.exchangeRateService = exchangeRateService;
    }


    @PostMapping
    public ResponseEntity<PurchaseTransaction> createTransaction(@Valid @RequestBody PurchaseTransaction transaction) {
        System.out.println("Recebido: " + transaction);
        PurchaseTransaction savedTransaction = repository.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping("/{id}/convert")
    public ResponseEntity<?> getConvertedTransaction(
            @PathVariable Long id,
            @RequestParam String currency) {

        Optional<PurchaseTransaction> transactionOptional = repository.findById(id);

        if (transactionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Transaction not found.");
        }

        PurchaseTransaction transaction = transactionOptional.get();

        // 1. Chamar a API para obter a taxa de câmbio
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currency, transaction.getTransactionDate());

        // 2. Calcular o valor convertido
        BigDecimal convertedAmount = transaction.getAmount().multiply(exchangeRate);

        // 3. Montar o DTO de resposta
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(transaction,convertedAmount,currency,exchangeRate);

        return ResponseEntity.ok(responseDTO);
    }
}
