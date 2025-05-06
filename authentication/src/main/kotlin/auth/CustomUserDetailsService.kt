package com.bankingapp.auth


import com.bankingapp.user.UserEntity
import com.bankingapp.user.UsersRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service



@Service
class CustomUserDetailsService(
    private val usersRepository: UsersRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user : UserEntity = usersRepository.findByUsername(username) ?:
        throw UsernameNotFoundException("User not found...")

        return User.builder()
            .username(user.username)
            .password(user.password)
            .build()
    }
}
