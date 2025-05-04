package authentication.user


import authentication.auth.RegisterRequest
import authentication.auth.RegistrationRequestDTO
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.math.BigDecimal



@Service
data class UsersService (
    private val usersRepository: UsersRepository,
    private val passwordEncoder: PasswordEncoder
){

    // This function is for user registration. It takes the username and password from the request,
    // hashes the password to keep it secure, creates a new user object, and saves it to the database.

    fun createUser(request: RegisterRequest): UserEntity {
        val myNewUserEntity = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password)  //password hashing
        )

        return usersRepository.save(myNewUserEntity)
    }
    fun registerUsers(request: RegistrationRequestDTO): UserEntity {
        if (usersRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val password = request.password

        if (password.length < 6) {
            throw IllegalArgumentException("Password must be at least 6 characters long")
        }
        if (!password.any { it.isUpperCase() }) {
            throw IllegalArgumentException("Password must contain at least one capital letter")
        }
        if (!password.any { it.isDigit() }) {
            throw IllegalArgumentException("Password must contain at least one number")
        }

        val user = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(password),
            name = request.name,
            balance = request.balance ?: BigDecimal.ZERO
        )

        return usersRepository.save(user)
    }


    // This function searches for a user by username and returns their ID
    fun findByUsername(username: String): Long {
        val user = usersRepository.findByUsername(username)
            ?: throw IllegalStateException("User not found with username: $username")

        return user.id ?: throw IllegalStateException("User has no id...")
    }

    //Returns the entire UserEntity
    fun getUserByUsername(username: String): UserEntity {
        return usersRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User with username $username not found.")
    }



//    fun findByUsername (username: String): Long
//    = usersRepository.findByUsername(username)?.id ?: throw IllegalStateException("User has no id...")

}




