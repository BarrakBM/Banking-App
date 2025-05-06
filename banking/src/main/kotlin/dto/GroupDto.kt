package com.bankingapp.banking.dto

import java.math.BigDecimal
import java.time.LocalDate

data class GroupDto(
    val name: String,
    val initialBalance: BigDecimal
)

data class GroupResponseDTO(
    val groupId: Long,
    val name: String,
    val balance: BigDecimal
)


data class AddGroupMemberRequestDTO(
    val groupId: Long,
    val userIdToAdd: Long
)

data class GroupMemberResponseDTO(
    val id: Long?,
    val userId: Long,
    val groupId: Long,
    val isAdmin: Boolean,
    val joinedAt: LocalDate
)

// Add this to your GroupDto.kt file
data class RemoveGroupMemberRequestDTO(
    val groupId: Long,
    val userIdToRemove: Long
)

data class FundGroupRequestDTO(
    val groupId: Long,

)