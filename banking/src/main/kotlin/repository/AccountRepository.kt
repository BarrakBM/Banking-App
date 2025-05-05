package com.example.repository

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface AccountRepository:JpaRepository<AccountRepository,Long>{
    fun findByUserId(userId: Long): AccountEntity
}

@Entity
@Table(name = "accounts")
data class AccountEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id: Long? = null,
    val userId: Long,
    val name: String,
    val balance: BigDecimal,
    val isActive: Boolean

){
constructor(): this(0,0,"", BigDecimal.ZERO,true)

}