package authentication.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.security.Principal

@RestController
@RequestMapping("/api/user")

class UserController(private val usersService: UsersService)
{

    // This controller lets us get the profile of the currently logged-in user
    // When someone sends a request to /api/user/info, we use the JWT token to get their username (via Principal)
    // Then we use that username to find the user in the database and return their full profile info
    // This is helpful when we want to show the user's profile after login — without needing to send their ID or username again

    @GetMapping("/info")
    fun getCurrentUser(principal: Principal): ResponseEntity<UserProfileResponse> {
        val user = usersService.getUserByUsername(principal.name)


        return ResponseEntity.ok(
            UserProfileResponse(id = user.id!!, username = user.username, balance = user.balance, name = user.name, isActive = user.isActive)
        )
    }
}

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val name: String?,
    val balance: BigDecimal,
    val isActive: Boolean
)