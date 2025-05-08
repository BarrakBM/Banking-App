package com.bankingapp.banking.repository

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface GroupMembersRepository:JpaRepository<GroupMembersEntity,Long>{
    fun findByUserId(userId: Long):GroupMembersEntity
    fun findByGroupId(groupId: Long):GroupMembersRepository
    // Add this method to your GroupMembersRepository interface
    fun findByUserIdAndGroupId(userId: Long, groupId: Long): GroupMembersEntity?
    fun existsByGroupIdAndUserId(groupId: Long, userId: Long): Boolean
}

@Entity
@Table(name = "group_members")
data class GroupMembersEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,
    val userId: Long,
    val groupId: Long,
    val isAdmin: Boolean,
    val joinedAt: LocalDate
) {
    constructor(): this(0,0,0,false, LocalDate.now())
}