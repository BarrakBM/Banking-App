package com.bankingapp.banking.dto

import com.bankingapp.banking.repository.AccountEntity
import java.math.BigDecimal
import java.time.LocalDate

data class GroupPaymentDTO(
    val groupId: Long,
    val amount: BigDecimal,
    val account: Long,
    val description: String
)


data class GroupPaymentResponseDTO(

    val groupName: String,
    val toAccount: String,
    val amount: BigDecimal,
    val description: String,
    val createdAt: LocalDate
)
