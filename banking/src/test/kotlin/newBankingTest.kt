package com.bankingapp.banking

import com.bankingapp.banking.client.AuthenticationClient
import com.bankingapp.banking.dto.*
import com.bankingapp.banking.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["src/test/resources/test-application.properties"]
)

@ActiveProfiles("test")
class NewBankingTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var groupMembersRepository: GroupMembersRepository

    @Autowired
    lateinit var groupsRepository: GroupsRepository

    @Autowired
    lateinit var groupTransactionsRepository: GroupTransactionsRepository

    @Autowired
    lateinit var userTransactionsRepository: UserTransactionsRepository

    private lateinit var token: String
    private lateinit var testUsername: String
    private lateinit var testPassword: String

    @BeforeEach
    fun setUp() {
        // Clean up databases before each test
        userTransactionsRepository.deleteAll()
        groupTransactionsRepository.deleteAll()
        groupMembersRepository.deleteAll()
        groupsRepository.deleteAll()
        accountRepository.deleteAll()

        // Generate unique username for each test
        testUsername = "testuser_${System.currentTimeMillis()}"
        testPassword = "TestPass123"

        // Create and login test user
        createTestUser()
        token = loginTestUser()

        // Create account for the user
        createAccountInfo()
    }

    private fun createTestUser() {
        val authCreateUrl = "http://localhost:8086/api/auth/register"
        val newUserRequest = mapOf(
            "username" to testUsername,
            "password" to testPassword
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(newUserRequest, headers)

        val createResponse = restTemplate.postForEntity(authCreateUrl, entity, Map::class.java)

        if (createResponse.statusCode != HttpStatus.OK && createResponse.statusCode != HttpStatus.CREATED) {
            throw IllegalStateException("Failed to create test user")
        }
    }

    private fun loginTestUser(): String {
        val authUrl = "http://localhost:8086/api/auth/login"
        val loginRequest = mapOf(
            "username" to testUsername,
            "password" to testPassword
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(loginRequest, headers)

        val response = restTemplate.postForEntity(authUrl, entity, Map::class.java)

        return response.body?.get("token") as String
    }

    private fun createAccountInfo() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val accountInfo = accountInformationDTO(
            name = testUsername,
            balance = BigDecimal(500.0)
        )

        val requestEntity = HttpEntity(accountInfo, headers)

        restTemplate.exchange(
            "/account/v1/addOrUpdateInformation",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
    }

    private fun getUserIdForTestUser(): Long {
        val authCheckUrl = "http://localhost:8081/api/auth/check-token"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val requestEntity = HttpEntity<String>("", headers)
        val response = restTemplate.exchange(
            authCheckUrl,
            HttpMethod.POST,
            requestEntity,
            CheckTokenResponse::class.java
        )

        return response.body!!.userId
    }

    @Test
    fun `As a user, I can update an account`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val accountInfo = accountInformationDTO(
            name = testUsername,
            balance = BigDecimal(200.0)
        )

        val requestEntity = HttpEntity(accountInfo, headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/account/v1/addOrUpdateInformation",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val savedAccount = accountRepository.findByUserId(getUserIdForTestUser())
        assertNotNull(savedAccount)
        assertEquals(testUsername, savedAccount?.name)
        assertEquals(0, savedAccount?.balance?.compareTo(BigDecimal(200.0)))
    }

    @Test
    fun `As a User, I can de-activate my account`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val deactivateRequest = HttpEntity(null, headers)
        val response = restTemplate.exchange(
            "/account/v1/deactive",
            HttpMethod.POST,
            deactivateRequest,
            String::class.java
        )






























































        assertEquals(HttpStatus.OK, response.statusCode)

        val account = accountRepository.findByname(testUsername)
        assertNotNull(account)
        assertFalse(account!!.isActive)
    }

    @Test
    fun `As a user, I can view my account information `(){
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val response = restTemplate.exchange(
            "/account/v1/GetInformation",
            HttpMethod.GET,
            HttpEntity<String>(null, headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

    }

    @Test
    fun `As an admin, I can deactivate the group`() {
        // Create group
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val groupDto = GroupDto(
            name = "Test Group",
            initialBalance = BigDecimal(1000)
        )

        val createResponse = restTemplate.exchange(
            "/groups/v1/create",
            HttpMethod.POST,
            HttpEntity(groupDto, headers),
            GroupResponseDTO::class.java
        )

        val groupId = createResponse.body?.groupId!!

        // Deactivate the group
        val deactivateRequest = mapOf("groupId" to groupId)
        val response = restTemplate.exchange(
            "/groups/v1/de-activate-group",
            HttpMethod.POST,
            HttpEntity(deactivateRequest, headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        // Verify in database
        val group = groupsRepository.findByGroupId(groupId)
        assertFalse(group?.isActive ?: true)
    }


    @Test
    fun `As a User, I can create a group`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val groupDto = GroupDto(
            name = "Test Group ${System.currentTimeMillis()}",
            initialBalance = BigDecimal(1000)
        )

        val requestEntity = HttpEntity(groupDto, headers)

        val response = restTemplate.exchange(
            "/groups/v1/create",
            HttpMethod.POST,
            requestEntity,
            GroupResponseDTO::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(groupDto.name, response.body?.name)
        assertEquals(0, groupDto.initialBalance.compareTo(response.body?.balance))
    }

    @Test
    fun `As an GroupAdmin, I can add a member to the group`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val groupDto = GroupDto(
            name = "Test Group ${System.currentTimeMillis()}",
            initialBalance = BigDecimal(1000)
        )

        val requestEntity = HttpEntity(groupDto, headers)

        val response = restTemplate.exchange(
            "/groups/v1/create",
            HttpMethod.POST,
            requestEntity,
            GroupResponseDTO::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(groupDto.name, response.body?.name)
        assertEquals(0, groupDto.initialBalance.compareTo(response.body?.balance))
    }
}

// Correct DTO for transfer
data class CheckTokenResponse(
    val userId: Long
)