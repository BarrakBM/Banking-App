package com.bankingapp.banking.services


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.repository.AccountEntity
import com.bankingapp.banking.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountServices (
    val accountRepository: AccountRepository
){

    fun addinformation(userId: Long,accountInfo: accountInformationDTO){


        val userInfo = AccountEntity(
            userId = userId,
            name = accountInfo.name,
            balance = accountInfo.balance,
            isActive = true
        )
        accountRepository.save(userInfo)
//        val saveInfo = accountRepository.save(userInfo)

//        val newInfo = AccountEntity(
//            userId = userId,
//            name = accountInfo.name,
//            balance = accountInfo.balance,
//            isActive = true
//        )
//        accountRepository.save(newInfo)
    }
}