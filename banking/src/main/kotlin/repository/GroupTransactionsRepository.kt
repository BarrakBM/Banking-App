package com.bankingapp.banking.repository

import  com.bankingapp.banking.repository.GroupsEntity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface GroupTransactionsRepository: JpaRepository<GroupTransactionsEntity,Long>{
    fun findById(id: Long?):GroupTransactionsEntity
    fun findByGroupId(groupId: GroupsEntity): GroupTransactionsEntity
    fun findByAccountId(accountId: AccountEntity): List<GroupTransactionsEntity>
}

@Entity
@Table(name = "group_transactions")
data class GroupTransactionsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "group_id")
    val groupId: GroupsEntity,
    val amount: BigDecimal,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "account_id")
    val accountId: AccountEntity,
    val createdAt: LocalDate

){
    constructor(): this(0,GroupsEntity(), BigDecimal.ZERO,"", AccountEntity(),LocalDate.now())
}