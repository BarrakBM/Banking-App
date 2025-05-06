package com.bankingapp.banking.services


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.repository.AccountEntity
import com.bankingapp.banking.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountServices (
    val accountRepository: AccountRepository
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

    fun deactiveAccount(userId: Long){

        println("the user iiiiiidddddddd is $userId")
            var account = accountRepository.findByUserId(userId)!!
            account.isActive = false
        accountRepository.save(account)

    }
}