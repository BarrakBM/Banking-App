package com.bankingapp.banking.services


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.dto.*
//import com.bankingapp.banking.dto.userTransactionHistoryRespone
import com.bankingapp.banking.repository.AccountEntity
import com.bankingapp.banking.repository.AccountRepository
import com.bankingapp.banking.repository.UserTransactionsRepository
import org.springframework.stereotype.Service

@Service
class AccountServices (
    val accountRepository: AccountRepository , val userTransactionsRepository: UserTransactionsRepository
){

    fun addOrUpdateInformation(userId: Long,accountInfo: accountInformationDTO){
        var account = accountRepository.findByUserId(userId) ?: AccountEntity(
            userId = userId,
            name = accountInfo.name,
            balance = accountInfo.balance,
            isActive = true
        )

        account.name = accountInfo.name
        account.balance = accountInfo.balance

        accountRepository.save(account)

    }

    fun deactiveAccount(userId: Long){                  // Exception handled in Service Side

            var account = accountRepository.findByUserId(userId)!!
            account.isActive = false
        accountRepository.save(account)

    }
//      this function is in progress
//    fun userFundGroup(userId: Long, fundgrp: fundGroupDTO){
//
//        val account = accountRepository.findByUserId(userId) ?: throw IllegalArgumentException("Account not found for userId: $userId")
//
//        if(account.balance < fundgrp.amount)
//        {
//
//        }
//
//    }


//      this function is in progress
//    fun userTransactionHistory(userId: Long): userTransactionHistoryRespone {
//        val accountId = accountRepository.findByUserId(userId)!!            // source Account         ?: throw IllegalArgumentException("Account not found for userId: $userId")
//
//        val transactions = userTransactionsRepository.findById(accountId.id)  //  ?: emptyList()
////        val transactionTo = accountRepository.findByUserId()
////
//        val historyList = transactions.map { transaction ->
//            userTransHistory(
//                fromUser = accountId.name,
//                toUser = transaction.destinationId.toString(),
//                amount = transaction.amount,
//                time = transaction.createdAt
//            )
//        }
//
//        return userTransactionHistoryRespone(historyList = historyList)
//    }





}