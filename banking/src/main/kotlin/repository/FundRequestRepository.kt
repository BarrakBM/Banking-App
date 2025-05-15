package com.bankingapp.banking.repository

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal


interface FundRequestRepository : JpaRepository<FundRequestEntity, Long>



@Entity
@Table(name = "fund_requests")
data class FundRequestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "account_id")
    val account: AccountEntity,

    val amount: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "group_id")
    val group: GroupsEntity
)

{
    constructor() : this(0, AccountEntity(), BigDecimal.ZERO, GroupsEntity())
}
