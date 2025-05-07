package com.bankingapp.banking.services


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.*
//import com.bankingapp.banking.dto.userTransactionHistoryRespone
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.BadRequest
import java.math.BigDecimal
import java.time.LocalDate

@Service
class AccountServices (
    val accountRepository: AccountRepository ,
    val userTransactionsRepository: UserTransactionsRepository,
    val groupsRepository: GroupsRepository
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


//    fun userFundGroup(userId: Long ,fundgrp: fundGroupDTO): BigDecimal{
////         check account available
//        val account = accountRepository.findByUserId(userId)!! //?: throw IllegalArgumentException("Account not found for userId: $userId")
//
////         check group available and Active
//        val group = groupsRepository.findByGroupId(fundgrp.groupId)!!// ?: throw IllegalArgumentException("group not found for groupId: $fundgrp.groupId")
//        if (!group.isActive)
//        {
//            throw IllegalArgumentException(" group is Not active")
//        }
//
////         check amount
//        if(account.balance < fundgrp.amount)
//        {
//            throw IllegalArgumentException("No sufficient balance")
//        }
//
////         increase group balance
//        group.balance = group.balance.add(fundgrp.amount)
//        groupsRepository.save(group)
//
////         decrease user amount
//        account.balance = account.balance.subtract(fundgrp.amount)
//        accountRepository.save(account)
//
////        adding to group transaction
//
////        add transaction into the user transaction
//        val transaction = UserTransactionsEntity(
//            sourceId = account.id,
//             destinationId =  fundgrp.,
//             amount = fundgrp.amount,
//            createdAt =  LocalDate.now()
//        )
//        userTransactionsRepository.save(transaction)
//
//        return account.balance
//
//    }



//    fun userTransactionHistory(userId: Long): userTransactionHistoryRespone? {
//        val accountId = accountRepository.findByUserId(userId)
//            ?: throw IllegalArgumentException("Account not found for userId: $userId")
//
//        val transactions = userTransactionsRepository.findById(accountId.id)
//            ?: throw IllegalArgumentException("user have no transactions")
//
//
//        val historyList = transactions.map { transaction ->
//            userTransactionDTO(
//                fromUser = accountId.name,
//                toUser = transaction.a,
//                amount = transaction.amount,
//                time = transaction.createdAt
//            )
//        }
//
//        return userTransactionHistoryRespone(historyList = historyList)
//    }





}