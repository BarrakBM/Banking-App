package com.bankingapp.banking.dto

import java.math.BigDecimal
import java.time.LocalDate


data class accountInformationDTO(

    val name: String,
    val balance: BigDecimal
    )

data class fundGroupDTO(
    val groupId: Long,
    val amount: BigDecimal,
    val description: String?
)


data class userTransHistory(
    val fromUser: String,
    val toUser: String,
    val amount: BigDecimal,
    val time: LocalDate
)

data class userTransactionHistoryRespone(
    val historyList: List<userTransHistory>
)
