package com.launchersoft.purchase_transaction.controller;

import com.launchersoft.purchase_transaction.dto.TransactionResponseDTO;
import com.launchersoft.purchase_transaction.entity.PurchaseTransaction;
import com.launchersoft.purchase_transaction.repository.PurchaseTransactionRepository;
import com.launchersoft.purchase_transaction.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PurchaseTransactionControllerTest {

    @Test
    void testGetConvertedTransaction_success() {
        // Mock dependencies
        PurchaseTransactionRepository repository = Mockito.mock(PurchaseTransactionRepository.class);
        ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
        PurchaseTransactionController controller = new PurchaseTransactionController(repository, exchangeRateService);

        // Mock data
        PurchaseTransaction transaction = new PurchaseTransaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTransactionDate(LocalDate.of(2024, 1, 1));

        BigDecimal exchangeRate = BigDecimal.valueOf(5.25); // Taxa de câmbio esperada
        BigDecimal convertedAmount = transaction.getAmount().multiply(exchangeRate);
        TransactionResponseDTO expectedResponse = new TransactionResponseDTO(transaction, convertedAmount, "USD", exchangeRate);

        when(repository.findById(1L)).thenReturn(Optional.of(transaction));
        when(exchangeRateService.getExchangeRate("USD", LocalDate.of(2024, 1, 1)))
                .thenReturn(exchangeRate);

        // Call method
        ResponseEntity<?> response = controller.getConvertedTransaction(1L, "USD");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        TransactionResponseDTO actualResponse = (TransactionResponseDTO) response.getBody();
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getFormattedConvertedAmount(), actualResponse.getFormattedConvertedAmount());
        assertEquals(expectedResponse.getExchangeRate(), actualResponse.getExchangeRate());
        assertEquals(expectedResponse.getOriginalAmount(), actualResponse.getOriginalAmount());
    }


    @Test
    void testGetConvertedTransaction_transactionNotFound() {
        // Mock dependencies
        PurchaseTransactionRepository repository = Mockito.mock(PurchaseTransactionRepository.class);
        ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
        PurchaseTransactionController controller = new PurchaseTransactionController(repository, exchangeRateService);

        // Mock no transaction found
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Call method
        ResponseEntity<?> response = controller.getConvertedTransaction(1L, "USD");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("Transaction not found.", response.getBody());
    }
}
