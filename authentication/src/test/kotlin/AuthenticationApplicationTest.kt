package com.bankingapp

import com.bankingapp.jwt.JwtService
import com.bankingapp.user.UserEntity
import com.bankingapp.user.UsersRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["src/test/resources/test-application.properties"]

)
@ActiveProfiles("test")
class AuthenticationApplicationTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtService: JwtService

    companion object {
        lateinit var jwtToken: String

        @JvmStatic
        @BeforeAll

        fun setUp(
            @Autowired usersRepository: UsersRepository,
            @Autowired passwordEncoder: PasswordEncoder,
            @Autowired jwtService: JwtService
        ) {
            // Clear any existing users
            usersRepository.deleteAll()

            // Create a test user
            val user = UserEntity(
                username = "FFN",
                password = passwordEncoder.encode("Fpassword@123")
            )

            usersRepository.save(user)

            // Generate a valid JWT for that user
            jwtToken = jwtService.generateToken(user.username)
        }
    }

    private fun authHeaders(): HttpHeaders = HttpHeaders().apply {
        set(HttpHeaders.AUTHORIZATION, "Bearer $jwtToken")
        contentType = MediaType.APPLICATION_JSON
    }


    @Test
    fun ` user can log in and get JWT token`() {
        val loginBody = mapOf("username" to "FFN", "password" to "Fpassword@123")
        val resp = restTemplate.postForEntity("/api/auth/login", loginBody, Map::class.java)
        // Assert: Verify the response status is 200 OK
        assertEquals(HttpStatus.OK, resp.statusCode)
        // Assert: Check that the response contains a non-null token field
        // This confirms the JWT token was successfully returned
        assertNotNull(resp.body?.get("token"))

    }



    @Test

    fun `user can register successfully`() {

        // Given ->  valid registration request
        val registrationBody = mapOf("username" to "Fatma123", "password" to "StrongP@123")
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val request = HttpEntity(registrationBody, headers)

        //sending POST request to /register
        val response = restTemplate.postForEntity("/api/auth/register", request, String::class.java)

        //assert status is 200 OK and response is "OK"
        assertEquals(HttpStatus.OK, response.statusCode)

        // Verify user was saved in DB
        val savedUser = usersRepository.findByUsername("Fatma123")
        assertNotNull(savedUser)

    }

}



