package com.bankingapp.banking.services

import com.bankingapp.banking.dto.GroupDto
import com.bankingapp.banking.dto.GroupPaymentDTO
import com.bankingapp.banking.dto.GroupResponseDTO
import com.bankingapp.banking.repository.*
import jakarta.inject.Named
import java.time.LocalDate

@Named
class GroupService(
    private val groupsRepository: GroupsRepository,
    private val groupMembersRepository: GroupMembersRepository,
    private val groupTransactionsRepository: GroupTransactionsRepository
) {

    // creating a group
    fun createGroup(userId: Long, groupDto: GroupDto): GroupResponseDTO {
        // create a new group with current user as an admin
        val newGroup = GroupsEntity(
            name = groupDto.name,
            balance = groupDto.initialBalance,
            isActive = true,
            adminId = userId
        )
        // save the group
        val savedGroup = groupsRepository.save(newGroup)

        // add the group creator as an admin and member
        val groupMember = GroupMembersEntity(
            userId = userId,
            groupId = savedGroup.groupId ?: throw IllegalArgumentException("group id can't be null"),
            isAdmin = true,
            joinedAt = LocalDate.now()
        )

        groupMembersRepository.save(groupMember)

        // return the group detail
        return GroupResponseDTO(
            groupId = savedGroup.groupId ?: 0,
            name = savedGroup.name,
            balance = savedGroup.balance,
        )
    }

    // adding member to the group
    fun addGroupMember(adminId: Long, groupId: Long, userIdToAdd: Long): GroupMembersEntity{
        // check if the group exists
        val group = groupsRepository.findByGroupId(groupId)
            ?: throw IllegalArgumentException("Group cannot be found")


        // check if current user is group admin
        if (group.adminId != adminId) {
            throw IllegalArgumentException("Only admin can add members to the group")
        }

        // check if user is already a member of the group
        val existingMember = groupMembersRepository.findByUserIdAndGroupId(userIdToAdd, groupId)
        if (existingMember != null)
            throw IllegalArgumentException("User is already a member of this group")

        // adding the user
        val newMember = GroupMembersEntity(
            userId = userIdToAdd,
            groupId = groupId,
            isAdmin = false,
            joinedAt = LocalDate.now()
        )
        return groupMembersRepository.save(newMember)

    }

    // remove group member
    fun removeGroupMember(adminId: Long, groupId: Long, userToRemove: Long) {

        // check if the group exists
        val group = groupsRepository.findByGroupId(groupId)
            ?: throw IllegalArgumentException("Group cannot be found")


        // check if current  user is group admin
        if (group.adminId != adminId) {
            throw IllegalArgumentException("Only admin can add members to the group")
        }


        // check if user is already a member of the group
        val existingMember = groupMembersRepository.findByUserIdAndGroupId(userToRemove, groupId)
            ?: throw IllegalArgumentException("User is not a member of this group")

        // check if user is admin (can't remove admin)
        if (existingMember.isAdmin) {
            throw IllegalArgumentException("Cannot remove the group admin")
        }

        //remove the user
        return groupMembersRepository.delete(existingMember)
    }


    // de-activate group
    fun deActivateGroup(adminId: Long, groupId: Long): GroupsEntity {
        // check if the group exists
        val group = groupsRepository.findByGroupId(groupId)
            ?: throw IllegalArgumentException("Group cannot be found")

        // check if current  user is group admin
        if (group.adminId != adminId) {
            throw IllegalArgumentException("Only admin can de-activate to the group")
        }

        val deActivateGroup = GroupsEntity(
            groupId = groupId,
            name = group.name,
            balance = group.balance,
            isActive = false,
            adminId = adminId
        )

        return groupsRepository.save(deActivateGroup)
    }


    // Admin paying for the group
    fun payForGroup(adminId: Long, paymentDTO: GroupPaymentDTO): GroupTransactionsEntity {
        // check if the group exists
        val group = groupsRepository.findByGroupId(paymentDTO.groupId)
            ?: throw IllegalArgumentException("group can't be found")


        // check if current  user is group admin
        if (group.adminId != adminId) {
            throw IllegalArgumentException("Only admin can add members to the group")
        }

        // check if group have sufficient amount
        if(group.balance < paymentDTO.amount){
            throw IllegalArgumentException("No sufficient balance")
        }

        val transaction = GroupTransactionsEntity(
            groupId = group,
            amount = paymentDTO.amount,
            description = paymentDTO.description,
            createdAt = LocalDate.now()
        )

        //update group balance
        group.balance = group.balance.subtract(paymentDTO.amount)
        groupsRepository.save(group)

        // save transaction
        return groupTransactionsRepository.save(transaction)
    }

}