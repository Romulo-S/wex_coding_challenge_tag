package com.launchersoft.purchase_transaction.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Service
public class ExchangeRateService {

    private static final String API_BASE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";

    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getExchangeRate(String currency, LocalDate date) {

        LocalDate minDate = date.minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String apiUrl = String.format(
                "%s?filter=record_date:lte:%s,record_date:gte:%s,currency:eq:%s&fields=exchange_rate,record_date",
                API_BASE_URL, date.format(formatter), minDate.format(formatter), currency
        );

        try {
            ResponseEntity<ExchangeRateResponseFromApi> response = restTemplate.getForEntity(apiUrl, ExchangeRateResponseFromApi.class);

            if (response.getBody() != null && !response.getBody().getData().isEmpty()) {
                ExchangeRateData latestData = response.getBody().getData().stream()
                        .max(Comparator.comparing(ExchangeRateData::getRecordDate))
                        .orElseThrow(() -> new RuntimeException("No exchange rate data found for the given date range."));

                return latestData.getExchangeRate();
            } else {
                throw new RuntimeException("No exchange rate data found for the given date range.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rate: " + e.getMessage(), e);
        }
    }

    // Classes auxiliares para mapear o JSON da API externa
    @Setter
    @Getter
    public static class ExchangeRateResponseFromApi {
        private java.util.List<ExchangeRateData> data;

    }

    @Setter
    @Getter
    public static class ExchangeRateData {
        private BigDecimal exchangeRate;
        private LocalDate recordDate;
        private String currency;
    }
}
