package com.bankingapp.banking.repository

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import javax.print.attribute.standard.Destination

@Repository
interface UserTransactionsRepository: JpaRepository<UserTransactionsEntity,Long>{
    fun findById(Id: Long?): List<UserTransactionsEntity>?
    fun findBySourceId(sourceId: AccountEntity): List<UserTransactionsEntity>
}

@Entity
@Table(name = "user_transactions" )
class UserTransactionsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id:Long? = null,
    @ManyToOne
    @JoinColumn(name = "source_id")
    val  sourceId:AccountEntity,
    @ManyToOne
    @JoinColumn(name = "destination_id")
    val destinationId: AccountEntity,
    val amount: BigDecimal,
    val createdAt: LocalDate
) {
    constructor(): this(0,AccountEntity(), AccountEntity(), BigDecimal.ZERO,LocalDate.now())
}