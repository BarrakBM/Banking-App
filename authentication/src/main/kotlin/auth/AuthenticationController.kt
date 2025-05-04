package authentication.auth

import authentication.jwt.JwtService
import authentication.user.UsersService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val usersService: UsersService,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) {


    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse {
        val authToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        val authentication = authenticationManager.authenticate(authToken)

        if (authentication.isAuthenticated) {
            val userDetails = userDetailsService.loadUserByUsername(authRequest.username)
            val token = jwtService.generateToken(userDetails.username)
            return AuthenticationResponse(token)
        } else {
            throw UsernameNotFoundException("Invalid user request!")
        }
    }

    @PostMapping("/register")

    fun addUser(@RequestBody request: RegisterRequest): ResponseEntity<AuthenticationResponse> {

        val user = usersService.createUser(request)
        // Calls a service method to create a new user entity in the database.
        // This might include hashing the password, saving user data, etc.

        val token = jwtService.generateToken(user.username)
        // After creating the user, generates a JWT token using the username.
        // This token is used for future authentication.

        return ResponseEntity.ok(AuthenticationResponse(token))
        // Returns HTTP 200 OK response with a JSON body containing the generated token.
    }


//    @PostMapping("/register")
//    fun addUser(@RequestBody request: RegisterRequest) {
//        usersService.createUser(request)
//        ResponseEntity.ok()
//    }

    @PostMapping("/check-token")
    fun checkToken(
        principal: Principal
    ): CheckTokenResponse {
        return CheckTokenResponse(
            userId = usersService.findByUsername(principal.name)
        )
    }
}

data class AuthenticationRequest(
    val username: String,
    val password: String
)

data class AuthenticationResponse(
    val token: String
)

data class CheckTokenResponse(
    val userId: Long
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class RegisterFailureResponse(
    val error: AddUserError
)

enum class AddUserError {
    INVALID_USERNAME, TOO_SHORT_PASSWORD, MAX_ACCOUNT_LIMIT_REACHED
}
