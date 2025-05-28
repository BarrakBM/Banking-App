package com.bankingapp.banking.repository

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface AccountRepository:JpaRepository<AccountEntity,Long>{
    fun findById(id: Long?): AccountEntity

    fun findByname(name: String): AccountEntity?
    fun findByUserId(userId: Long): AccountEntity?
}

@Entity
@Table(name = "accounts")
data class AccountEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: Long,
    var name: String,
    var balance: BigDecimal,
    var isActive: Boolean,
    var gender: Int?

){
constructor(): this(0,0,"", BigDecimal.ZERO,true , 0)

}