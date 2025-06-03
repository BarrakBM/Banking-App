package com.bankingapp.banking.services

import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.*
import jakarta.inject.Named
import java.math.RoundingMode
import java.time.LocalDate

@Named
class GroupService(
    private val groupsRepository: GroupsRepository,
    private val groupMembersRepository: GroupMembersRepository,
    private val groupTransactionsRepository: GroupTransactionsRepository,
    private val accountRepository: AccountRepository,
    private val fundRequestRepository: FundRequestRepository
) {

    // Get all groups where user is a member
    fun getUserGroups(userId: Long): List<GroupDetailsDTO> {
        // Find all group memberships for this user
        val userGroupMemberships = groupMembersRepository.findAll().filter { it.userId == userId }

        return userGroupMemberships.mapNotNull { membership ->
            try {
                // Get the group details
                val group = groupsRepository.findByGroupId(membership.groupId)
                if (group != null && group.isActive) {
                    // Get all members of this group
                    val allMembers = groupMembersRepository.findByGroupId(membership.groupId)
                    val memberDTOs = allMembers.map { member ->
                        // Fetch user name for each member
                        val memberAccount = accountRepository.findByUserId(member.userId)
                        MemberDTO(
                            userId = member.userId,
                            userName = memberAccount?.name ?: "Unknown User",
                            isAdmin = member.isAdmin
                        )
                    }

                    // Get admin name
                    val adminAccount = accountRepository.findByUserId(group.adminId)

                    GroupDetailsDTO(
                        groupId = group.groupId!!,
                        groupName = group.name,
                        balance = group.balance,
                        adminId = group.adminId,
                        adminName = adminAccount?.name ?: "Unknown Admin",
                        members = memberDTOs
                    )
                } else {
                    null // Skip inactive groups
                }
            } catch (e: Exception) {
                null // Skip groups that can't be loaded
            }
        }
    }

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

    fun createGroupWithMembers(userId: Long, groupDto: GroupWithMembersDto): GroupResponseDTO {
        val account = accountRepository.findByUserId(userId) ?: throw IllegalStateException("User doesn't have an account")
        if (!account.isActive){
            throw IllegalArgumentException("Account is not active")
        }

        // Create new group with current user as admin
        val newGroup = GroupsEntity(
            name = groupDto.name,
            balance = groupDto.initialBalance,
            isActive = true,
            adminId = userId
        )
        val savedGroup = groupsRepository.save(newGroup)

        // Add the group creator as admin
        val creatorMember = GroupMembersEntity(
            userId = userId,
            groupId = savedGroup.groupId ?: throw IllegalArgumentException("Group ID cannot be null"),
            isAdmin = true,
            joinedAt = LocalDate.now()
        )
        groupMembersRepository.save(creatorMember)

        // Add other selected members
        groupDto.memberIds.forEach { memberId ->
            val member = GroupMembersEntity(
                userId = memberId,
                groupId = savedGroup.groupId,
                isAdmin = false,
                joinedAt = LocalDate.now()
            )
            groupMembersRepository.save(member)
        }

        return GroupResponseDTO(
            groupId = savedGroup.groupId,
            name = savedGroup.name,
            balance = savedGroup.balance
        )
    }

    // adding member to the group
    fun addGroupMember(adminId: Long, groupId: Long, userIdToAdd: Long): AddGroupMemberResposeDTO {
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
            ?: throw IllegalArgumentException("No account for this user")

        if (!currentUser.isActive ){
            throw IllegalArgumentException("User account is not active")
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

        val response = AddGroupMemberResposeDTO(
            username = currentUser.name,
            groupname = group.name,
            joinedAt = newMember.joinedAt
        )
        groupMembersRepository.save(newMember)

        return response


    }

    // remove group member
    fun removeGroupMember(adminId: Long, groupId: Long, userToRemove: Long): userRemovedResponseDTO {

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

        val account = accountRepository.findByUserId(userToRemove)
            ?: throw IllegalArgumentException("User Account Not Fund")

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
        val removedUser1 = userRemovedResponseDTO(
            groupName = group.name,
            username = account.name
        )
        return removedUser1

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
            throw IllegalArgumentException("Only admin can pay for the group")
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
        val memberDTOs = members.map { member ->
            // Fetch user name for each member
            val memberAccount = accountRepository.findByUserId(member.userId)
            MemberDTO(
                userId = member.userId,
                userName = memberAccount?.name ?: "Unknown User",
                isAdmin = member.isAdmin
            )
        }

        // Get admin name
        val adminAccount = accountRepository.findByUserId(group.adminId)

        return GroupDetailsDTO(
            groupId = group.groupId!!,
            groupName = group.name,
            balance = group.balance,
            adminId = group.adminId,
            adminName = adminAccount?.name ?: "Unknown Admin",
            members = memberDTOs
        )
    }

    // check balance --
    fun adminFundRequest(userId: Long , adminRequest: adminFundRequestDTO){

        val group = groupsRepository.findByGroupId(adminRequest.groupId)
            ?: throw IllegalArgumentException("Group cannot be found")


        // check if current  user is group admin
        if (group.adminId != userId) {
            throw IllegalArgumentException("Only admin can ask for Request")
        }

        if(!group.isActive){
            throw IllegalArgumentException("Group is not active")
        }

        val members = groupMembersRepository.findByGroupId(adminRequest.groupId)

        val userAmount = adminRequest.amount.setScale(3, RoundingMode.UP).div(members.size.toBigDecimal())

        // insert user amount to DB
        for (m in members)
        {
            val account = accountRepository.findByUserId(m.userId)
                ?: throw IllegalArgumentException("No account avliable")

            val fundEntity = FundRequestEntity(
                account = account,
                amount = userAmount,
                group = group

            )
            val fundRequest = fundRequestRepository.save(fundEntity)
        }

    }

}