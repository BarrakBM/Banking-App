package com.bankingapp.banking.dto

import java.math.BigDecimal
import java.time.LocalDate

data class GroupDto(
    val name: String,
    val initialBalance: BigDecimal
)

data class GroupWithMembersDto(
    val name: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
    val memberIds: List<Long> = emptyList()
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

data class AddGroupMemberResposeDTO(
    val groupname: String,
    val username: String,
    val joinedAt: LocalDate
)

data class GroupMemberResponseDTO(
    val id: Long?,
    val userId: Long,
    val groupId: Long,
    val isAdmin: Boolean,
    val joinedAt: LocalDate
)

data class RemoveGroupMemberRequestDTO(
    val groupId: Long,
    val userIdToRemove: Long
)

data class userRemoved(
    val groupId: Long,
    val RemovedUserId: Long
)

data class userRemovedResponseDTO(
    val groupName: String,
    val username: String
)

data class FundGroupRequestDTO(
    val groupId: Long,
)

data class deActivateGroupRequestDTO(
    val groupId: Long,
)

//for group details info - UPDATED WITH USER NAMES
data class GroupDetailsDTO(
    val groupId: Long,
    val groupName: String,
    val balance: BigDecimal,
    val adminId: Long,
    val adminName: String, // Added admin name
    val members: List<MemberDTO>
)

data class GroupDetailsResponseDTO(
    val groupName: String,
    val balance: BigDecimal,
    val adminName: String,
    val members: List<MemberInfoDTO>
)

// UPDATED WITH USER NAME
data class MemberDTO(
    val userId: Long,
    val userName: String, // Added user name
    val isAdmin: Boolean
)

data class MemberInfoDTO(
    val username: String,
    val isAdmin: Boolean
)

data class GroupIdRequestDTO(
    val groupId: Long
)

data class adminFundRequestDTO(
    val groupId: Long,
    val amount: BigDecimal
)