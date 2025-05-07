package com.bankingapp.banking.dto

import java.math.BigDecimal
import java.time.LocalDate

data class GroupPaymentDTO(
    val groupId: Long,
    val amount: BigDecimal,
    val description: String
)

data class GroupPaymentResponseDTO(
    val transactionId: Long?,
    val groupId: Long,
    val amount: BigDecimal,
    val description: String,
    val createdAt: LocalDate
)
