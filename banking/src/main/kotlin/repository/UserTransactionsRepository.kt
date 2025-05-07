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
}

@Entity
@Table(name = "user_transactions" )
class UserTransactionsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id:Long? = null,
    val  sourceId:Long,
    val destinationId: Long,
    val amount: BigDecimal,
    val createdAt: LocalDate
) {
    constructor(): this(0,0,0, BigDecimal.ZERO,LocalDate.now())
}