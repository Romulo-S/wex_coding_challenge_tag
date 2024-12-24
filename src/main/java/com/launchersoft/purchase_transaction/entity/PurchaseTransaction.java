package com.launchersoft.purchase_transaction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PURCHASE_TRANSACTION")
@Getter
@Setter
public class PurchaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Descrição não pode ser nula.")
    @Column(length = 50, nullable = false)
    private String description;

    @NotNull(message = "A data da transação não pode ser nula.")
    @Column(nullable = false)
    private LocalDate transactionDate;

    @NotNull(message = "O campo 'amount' é obrigatório.")
    @Column(nullable = false)
    private BigDecimal amount;
}
