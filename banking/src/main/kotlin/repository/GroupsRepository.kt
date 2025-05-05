package banking.repository


import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
interface GroupsRepository: JpaRepository<GroupsEntity,Long>{
    fun findByGroupId(groupId: Long?):GroupsEntity
    fun findByAdminId(adminId: Long):GroupsEntity
}

@Entity
@Table(name = "groups")
data class GroupsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val groupId: Long ? = null,
    val name: String,
    val balance:BigDecimal,
    val isActive: Boolean,
    val adminId: Long,

) {constructor(): this (0 , "", BigDecimal.ZERO,true,0)

}