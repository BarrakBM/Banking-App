package com.bankingapp.banking.services

import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.*
import jakarta.inject.Named
import java.time.LocalDate

@Named
class GroupService(
    private val groupsRepository: GroupsRepository,
    private val groupMembersRepository: GroupMembersRepository,
    private val groupTransactionsRepository: GroupTransactionsRepository,
    private val accountRepository: AccountRepository
) {

    // creating a group
    fun createGroup(userId: Long, groupDto: GroupDto): GroupResponseDTO {
        val account = accountRepository.findByUserId(userId) ?: throw IllegalStateException("User doesn't have an account")
        if (!account.isActive){
            throw IllegalArgumentException("Account is not active")
        }

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

        // check if group is acrive
        if(!group.isActive){
            throw IllegalArgumentException("Group is not active")
        }

        // check if user account is active
        val currentUser = accountRepository.findByUserId(userIdToAdd)
        if (currentUser != null) {
            if (!currentUser.isActive ){
                throw IllegalArgumentException("User account is not active")
            }
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
    fun removeGroupMember(adminId: Long, groupId: Long, userToRemove: Long): userRemoved {

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
        groupMembersRepository.delete(existingMember)

        val removedUser = userRemoved(
            groupId = groupId,
            RemovedUserId = userToRemove
        )
        return removedUser

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

        // check if group is de-activated
        if(!group.isActive){
            throw IllegalArgumentException("Group is not active")
        }

        val deActivateGroup = GroupsEntity(
            groupId = groupId,
            name = group.name,
            balance = group.balance,
            isActive = false,
            adminId = adminId
        )

        groupsRepository.save(deActivateGroup)

        return deActivateGroup
    }


    // Admin paying for the group
    fun payForGroup(adminId: Long, paymentDTO: GroupPaymentDTO): GroupPaymentResponseDTO {
        // check if the group exists
        val group = groupsRepository.findByGroupId(paymentDTO.groupId)
            ?: throw IllegalArgumentException("group can't be found")


        // check if current  user is group admin
        if (group.adminId != adminId) {
            throw IllegalArgumentException("Only admin can add members to the group")
        }

        if(!group.isActive){
            throw IllegalArgumentException("Group is not active")
        }

        // check if group have sufficient amount
        if(group.balance < paymentDTO.amount){
            throw IllegalArgumentException("No sufficient balance")
        }

        val paidAccount = accountRepository.findByUserId(paymentDTO.account)!!


        val transaction = GroupTransactionsEntity(
            groupId = group,
            amount = paymentDTO.amount,
            description = paymentDTO.description,
            accountId = paidAccount,
            createdAt = LocalDate.now()
        )

        groupTransactionsRepository.save(transaction)
        val payment = GroupPaymentResponseDTO(
            groupName = group.name,
            toAccount = paidAccount.name,
            amount = paymentDTO.amount,
            description = paymentDTO.description,
            createdAt = transaction.createdAt
        )

        //update group balance
        group.balance = group.balance.subtract(paymentDTO.amount)
        groupsRepository.save(group)

        // save transaction
        return payment
    }

    // user get group details
    fun getGroupDetails(requestingUserId: Long, groupId: Long): GroupDetailsDTO {

        //try to find the group by its ID using the groups repo , if the group dose not exist throw an error
        val group = groupsRepository.findById(groupId).orElseThrow { IllegalArgumentException("Group not found") }

        //check if the user who made the request is a member of this group (for security so only members can view group details)
        val isMember = groupMembersRepository.existsByGroupIdAndUserId(groupId, requestingUserId)

        //if the user is not a member we block access to the group details
        if (!isMember) { throw IllegalAccessException("Sorry, You are not a member of this group") }



        // Get all members of the group Here we fetch all group members and filter them by the current group ID
        val members = groupMembersRepository.findAll().filter { it.groupId == groupId }
        val memberDTOs = members.map { MemberDTO(userId = it.userId, isAdmin = it.isAdmin) }

        return GroupDetailsDTO(
            groupId = group.groupId!!,
            groupName = group.name,
            balance = group.balance,
            adminId = group.adminId,
            members = memberDTOs
        )
    }

}