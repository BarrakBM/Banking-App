package com.bankingapp.banking.controller


import com.bankingapp.banking.dto.TransferInfoDTO
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
    ): ResponseEntity<*> {
        val userId = request.getAttribute("userId") as Long
//         accountServices.addOrUpdateInformation(userId, accountInfo)

        return ResponseEntity.ok(accountServices.addOrUpdateInformation(userId,accountInfo))
    }

    @GetMapping("/account/v1/GetInformation")
    fun viewInformation(
        request: HttpServletRequest,

    ): ResponseEntity<*> {
        val userId = request.getAttribute("userId") as Long
//         accountServices.addOrUpdateInformation(userId, accountInfo)

        return try {
            ResponseEntity.ok(accountServices.viewInformation(userId))
        }
        catch (e: Exception)
        {
            ResponseEntity.badRequest().body("No account ")
        }
    }


    @GetMapping("/account/v1/allUsers")
    fun getAllUsers(request: HttpServletRequest): ResponseEntity<*> {

        val userId = request.getAttribute("userId") as Long
        return ResponseEntity.ok(accountServices.allUsers(userId))

    }



    @PostMapping("/account/v1/deactive")
    fun deactiveAccount(request: HttpServletRequest): ResponseEntity<*> {
        return try {
            val userId = request.getAttribute("userId") as Long
            ResponseEntity.ok(accountServices.deActiveAccount(userId))
        }catch (e: Exception)
        {
            println(e)
             ResponseEntity.badRequest().body("Bad Request =((")
        }

    }

    @PostMapping("/account/v1/fundGroup")
    fun userFundGroup(request: HttpServletRequest,
                     @RequestBody fundgrp: fundGroupDTO): ResponseEntity<*>{
       return try {
        val userId = request.getAttribute("userId") as Long
//        val userId = request.getAttribute("userId") as Long

            return ResponseEntity.ok(accountServices.userFundGroup(userId,fundgrp))
       }
       catch (e: Exception)
       {
        ResponseEntity.badRequest().body(mapOf("Sorry = ((( " to e.message))
       }


    }

    @GetMapping("/account/v1/userTransactionHistory")
    fun userTransactionHistory(request: HttpServletRequest): ResponseEntity<*> {
      return try {
          val userId = request.getAttribute("userId") as Long
          ResponseEntity.ok(accountServices.userTransactionHistory(userId))
      }
      catch (e: Exception)
      {
          ResponseEntity.badRequest().body(mapOf("Sorry = ((( " to e.message))
      }
    }


    @PostMapping("/account/v1/transfer")
    fun transferMoney(request: HttpServletRequest, @RequestBody transferInfo: TransferInfoDTO): ResponseEntity<*> {
        val userId = request.getAttribute("userId") as Long

        return ResponseEntity.ok(accountServices.transferMoney(
            userId,
            transferInfo.destinationId,
            transferInfo.amount))
    }


}