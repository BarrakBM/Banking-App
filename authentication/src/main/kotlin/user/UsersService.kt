package authentication.user


import authentication.auth.RegisterRequest
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




