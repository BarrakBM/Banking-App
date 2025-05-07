package com.bankingapp.banking.controller


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.dto.fundGroupDTO
import com.bankingapp.banking.dto.userTransactionHistoryRespone
import com.bankingapp.banking.services.AccountServices
import jakarta.servlet.http.HttpServletRequest
import org.apache.coyote.BadRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.BadRequest
import java.math.BigDecimal

@RestController

class AccountController (
    val accountServices: AccountServices
) {

    @PostMapping("/account/v1/addOrUpdateInformation")
    fun addInformation(
        request: HttpServletRequest,
        @RequestBody accountInfo: accountInformationDTO
    ){
        val userId = request.getAttribute("userId") as Long
         accountServices.addOrUpdateInformation(userId, accountInfo)

    }



    @PostMapping("/account/v1/deactive")
    fun deactiveAccount(request: HttpServletRequest): ResponseEntity<*> {
        return try {
            val userId = request.getAttribute("userId") as Long
            accountServices.deactiveAccount(userId)
             ResponseEntity.ok("")
        }catch (e: Exception)
        {
            println(e)
             ResponseEntity.badRequest().body("Bad Request =((")
        }

    }

    @PostMapping("/account/v1/fundGroup")
    fun userFundGroup(request: HttpServletRequest,
                     @RequestBody fundgrp: fundGroupDTO): BigDecimal{
//       return try {
        val userId = request.getAttribute("userId") as Long
//        val userId = request.getAttribute("userId") as Long
        return accountServices.userFundGroup(userId,fundgrp)
//            return ResponseEntity.ok(accountServices.userFundGroup(userId,fundgrp))
//       }
//       catch (e: Exception)
//       {
//        ResponseEntity.badRequest().body(mapOf("Sorry = ((( " to e.message))
//       }


    }

    @GetMapping("/account/v1/userTransactionHistory")
    fun userTransactionHistory(request: HttpServletRequest): userTransactionHistoryRespone? {
        val userId = request.getAttribute("userId") as Long
        return accountServices.userTransactionHistory(userId)
//        ResponseEntity.ok(accountServices.u)
    }



}