package com.bankingapp.banking.controller


import com.bankingapp.banking.dto.FundRequestResponseListDTO
import com.bankingapp.banking.services.FundRequestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FundRequestController(
    private val fundRequestService: FundRequestService
) {

    @GetMapping("/v1/fund-requests")
    fun getAllFundRequests(): FundRequestResponseListDTO {

        return fundRequestService.getAllFundRequests()
    }
}

