package com.bankingapp.banking.services


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class AccountServices (
    val accountRepository: AccountRepository ,
    val userTransactionsRepository: UserTransactionsRepository,
    val groupTransactionsRepository: GroupTransactionsRepository,
    val groupsRepository: GroupsRepository
){
    // DONE
    fun addOrUpdateInformation(userId: Long,accountInfo: accountInformationDTO): accountInformationDTO {
        val account = accountRepository.findByUserId(userId)
            ?: AccountEntity(
                userId = userId,
                name = accountInfo.name,
                balance = accountInfo.balance,
                isActive = true,
//                gender = accountInfo.gender!!

            )


        account.name = accountInfo.name // account name
        account.balance = accountInfo.balance // account new balance
//        account.gender = accountInfo.gender
        accountRepository.save(account)

        val newInfo = accountInformationDTO(
            name = account.name,
            balance = account.balance ,
//            gender = account.gender

        )

        return newInfo

    }


    fun viewInformation(userId: Long): InformationDTO {
        val account = accountRepository.findByUserId(userId)!!


        val newInfo = InformationDTO(
            name = account.name,
            balance = account.balance,
            isActive = account.isActive,
//            gender = account.gender!!
        )

        return newInfo

    }

    // DONE
    fun deActiveAccount(userId: Long): DeAtivateDTO {                  // Exception handled in Service Side

        var account = accountRepository.findByUserId(userId)!!
        account.isActive = false
        accountRepository.save(account)

        val accountDeActivated = DeAtivateDTO(
            userId = userId,
            isActive = account.isActive
        )
        return accountDeActivated
    }


    fun userFundGroup(userId: Long ,fundgrp: fundGroupDTO): userfundResponse{
//         check account available
        val account = accountRepository.findByUserId(userId)!! //?: throw IllegalArgumentException("Account not found for userId: $userId")

        // check if account is active
        if (!account.isActive){
            throw IllegalArgumentException("User is not active")
        }
//         check group available and Active
        val group = groupsRepository.findByGroupId(fundgrp.groupId)!!// ?: throw IllegalArgumentException("group not found for groupId: $fundgrp.groupId")
//        val groupAccount = accountRepository.findById()
        if (!group.isActive)
        {
            throw IllegalArgumentException(" group is Not active")
        }

//         check amount
        if(account.balance < fundgrp.amount)
        {
            throw IllegalArgumentException("No sufficient balance")
        }

//         increase group balance
        group.balance = group.balance.add(fundgrp.amount)
        groupsRepository.save(group)

//         decrease user amount
        account.balance = account.balance.subtract(fundgrp.amount)
        accountRepository.save(account)

//       adding to group transaction
        val transaction = GroupTransactionsEntity(
            groupId = group,
            amount = fundgrp.amount,
            description = fundgrp.description,
            accountId = account,
            createdAt = LocalDate.now()
        )
        groupTransactionsRepository.save(transaction)

        // user and group balance after funding
        val amounts = userfundResponse(
            userAmount =account.balance,
            groupAmount = group.balance
        )
        return amounts

    }


    fun userTransactionHistory(userId: Long): allTransactionHistoryRespone {
        val account = accountRepository.findByUserId(userId)!!
        //    ?: throw IllegalArgumentException("Account not found for userId: $userId")


        val transactions = userTransactionsRepository.findBySourceId(account)
        //   val transactions = userTransactionsRepository.findById(account.id)!!
        //  ?: throw IllegalArgumentException("user have no transactions")

        val userHistoryList = transactions.map { transaction ->
            allTransactionDTO(
                from = account.name,
                to = transaction.destinationId.name,
                amount = transaction.amount,
                type = "user",
                time = transaction.createdAt
            )

        }

        val groupTransactions = groupTransactionsRepository.findByAccountId(account)

        val groupHistoryList = groupTransactions.map { groupTransaction ->
            allTransactionDTO(
                from = account.name,
                to = groupTransaction.groupId.name,
                amount = groupTransaction.amount,
                type = "group",
                time = groupTransaction.createdAt
            )
        }

        var allHistory = userHistoryList+groupHistoryList

        return allTransactionHistoryRespone(transactionHistory = allHistory)
    }


    fun transferMoney(sourceAccountId: Long, destinationAccountId: Long, amount: BigDecimal): TransferResponseDTO   {

        val account = accountRepository.findByUserId(userId = sourceAccountId)!! //?: throw IllegalArgumentException("Account not found for userId: $userId")


        val destinationAccount = accountRepository.findByUserId(destinationAccountId)!!


        if (!account.isActive || !destinationAccount.isActive) {
            throw IllegalStateException("One of the accounts is inactive")
        }

        if (amount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Transfer amount must be greater than zero")
        }



        if (account.balance < amount) {
            throw IllegalStateException("Insufficient funds: balance is ${account.balance}, tried to transfer $amount")
        }

        if(account == destinationAccount){
            throw IllegalArgumentException("You can't transfer to yourself")
        }

        // Perform balance transfer
        account.balance -= amount //deduct the amount from the source account
        destinationAccount.balance += amount //add the amount to the destination account

        accountRepository.save(account)
        accountRepository.save(destinationAccount)

        // Save transaction record
        val new_transaction = userTransactionsRepository.save(
            UserTransactionsEntity(
                sourceId = account,
                destinationId = destinationAccount,
                amount = amount,
                createdAt = LocalDate.now()
            )
        )
        val transaction = TransferResponseDTO(
            userId = sourceAccountId,
            newBalance = account.balance
        )
        return transaction
    }

}