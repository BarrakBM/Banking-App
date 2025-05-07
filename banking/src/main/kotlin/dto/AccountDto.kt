package com.bankingapp.banking.dto

import com.bankingapp.banking.repository.UserTransactionsEntity
import java.math.BigDecimal
import java.time.LocalDate


data class accountInformationDTO(

    val name: String,
    val balance: BigDecimal
    )

data class fundGroupDTO(
    val groupId: Long,
    val amount: BigDecimal,
    val description: String
)


data class userTransactionHistoryRespone(
    val historyList: List<userTransactionDTO>
)


data class userTransactionDTO(
    val fromUser: Long,
    val toUser: Long,
    val amount: BigDecimal,
    val time: LocalDate
)

data class allTransactionDTO(
    val from: Long,
    val to: Long,
    val amount: BigDecimal,
    val type: String,
    val time: LocalDate
)


//data class userTransDTO(user: userTransactionDTO)(
//    val transaction: UserTransactionsEntity()
//)