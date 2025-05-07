package com.bankingapp.banking.controller


import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.dto.fundGroupDTO
import com.bankingapp.banking.services.AccountServices
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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

//    @PostMapping("/account/v1/fundGroup")
//    fun userFundGroup(request: HttpServletRequest,
//                      fundgrp: fundGroupDTO){
//        val userId = request.getAttribute("userId") as Long
//        accountServices.userFundGroup(userId,fundgrp)
//    }

//    @GetMapping("/account/v1/userTransactionHistory")
//    fun userTransactionHistory(request: HttpServletRequest): ResponseEntity<*>{
//        val userId = request.getAttribute("userId") as Long
////        ResponseEntity.ok(accountServices.u)
//    }



}