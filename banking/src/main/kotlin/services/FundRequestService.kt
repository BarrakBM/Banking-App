package com.bankingapp.banking.services


import com.bankingapp.banking.dto.FundRequestResponseDTO
import com.bankingapp.banking.dto.FundRequestResponseListDTO
import com.bankingapp.banking.repository.FundRequestRepository
import org.springframework.stereotype.Service

@Service
class FundRequestService(
    private val fundRequestRepository: FundRequestRepository
) {


    fun getAllFundRequests(): FundRequestResponseListDTO {
        // fetch all fund requests from the db
        val fundRequests = fundRequestRepository.findAll()

        // Create an empty list to hold the FundRequestResponseDTO
        val fundRequestDTOList = mutableListOf<FundRequestResponseDTO>()

        // manually iterate through each FundRequestEntity and convert it to FundRequestResponseDTO
        for (fundRequest in fundRequests) {

            val fundRequestDTO = FundRequestResponseDTO(
                groupName = fundRequest.group.name,  //access group name from FundRequestEntity
                amount = fundRequest.amount,
                accountName = fundRequest.account.name
            )

            //add the FundRequestResponseDTO to the list
            fundRequestDTOList.add(fundRequestDTO)
        }

        // Return FundRequestResponseListDTO
        return FundRequestResponseListDTO(fundRequests = fundRequestDTOList)
    }
}
