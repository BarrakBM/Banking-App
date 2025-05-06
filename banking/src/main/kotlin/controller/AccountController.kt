package com.bankingapp.banking.controller


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.services.AccountServices
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController

class AccountController (
    val accountServices: AccountServices
) {

    @PostMapping("/account/v1/AddInformation")
    fun addinformation(
        request: HttpServletRequest,
        @RequestBody accountInfo: accountInformationDTO
    ){
        println("test")
        val userId = request.getAttribute("userId") as Long
        val info = accountServices.addinformation(userId, accountInfo)

    }

}