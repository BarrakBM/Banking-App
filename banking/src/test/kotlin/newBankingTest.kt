package com.bankingapp.banking

import com.bankingapp.banking.Banking
import com.bankingapp.banking.dto.GroupResponseDTO
import com.bankingapp.banking.dto.TransferInfoDTO
import com.bankingapp.banking.dto.accountInformationDTO
import com.bankingapp.banking.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["src/test/resources/test-application.properties"]
)

@ActiveProfiles("test")
class BankingApplicationTest {

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

    private lateinit var token: String // store JWT token

    @BeforeEach
    fun setUp() {
        // Clean up databases to ensure no data from previous tests remains
        groupTransactionsRepository.deleteAll()
        userTransactionsRepository.deleteAll()
        groupMembersRepository.deleteAll()
        groupsRepository.deleteAll()
        accountRepository.deleteAll()

        // simulate logging in to get a JWT token
        val authUrl = "http://localhost:8080/api/auth/login"
        val loginRequest = mapOf("username" to "user2", "password" to "Pass123")
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity(loginRequest, headers)
        val response = restTemplate.postForEntity(authUrl, entity, Map::class.java)
        token = response.body?.get("token") as String
    }

    @Test
    fun `addOrUpdateInformation endpoint should create or update account for user`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token) // Use the retrieved token
        }

        // Creating account information DTO object with test data
        val accountInfo = accountInformationDTO(
            name = "user2",
            balance = BigDecimal(200.0)
        )
        //Trigger
        val requestEntity = HttpEntity(accountInfo, headers)
        val response = restTemplate.exchange(
            "/account/v1/addOrUpdateInformation",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        //Assert
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deactiveAccount endpoint should deactivate the user's account`() {
        //add or update account so there's something to deactivate
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val accountInfo = accountInformationDTO(
            name = "user2",
            balance = BigDecimal(200.0)
        )

        val createRequest = HttpEntity(accountInfo, headers)
        restTemplate.exchange(
            "/account/v1/addOrUpdateInformation",
            HttpMethod.POST,
            createRequest,
            String::class.java
        )

        //call the deactivation endpoint
        val deactivateRequest = HttpEntity(null, headers)
        val response = restTemplate.exchange(
            "/account/v1/deactive",
            HttpMethod.POST,
            deactivateRequest,
            String::class.java
        )

        //validate the response and that account is deactivated
        assertEquals(HttpStatus.OK, response.statusCode)

        val account = accountRepository.findByname("user2")
        assertThat(account).isNotNull
        assertThat(account!!.isActive).isFalse
    }

    @Test
    fun `transferMoney endpoint should transfer funds between accounts`() {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        // Create source account with sufficient balance
        val sourceAccount = accountRepository.save(
            AccountEntity(
                userId = 1001L,
                name = "sourceUser",
                balance = BigDecimal(500),
                isActive = true
            )
        )

        // Create destination account
        val destinationAccount = accountRepository.save(
            AccountEntity(
                userId = 1002L,
                name = "destUser",
                balance = BigDecimal(100),
                isActive = true
            )
        )

        // Build request
        val transferInfo = TransferInfoDTO(
            destinationId = destinationAccount.id!!,
            amount = 150.0.toBigDecimal()
        )

        val requestEntity = HttpEntity(transferInfo, headers)

        val response = restTemplate.exchange(
            "/api/transactions/transfer",
            HttpMethod.POST,
            requestEntity,
            Map::class.java
        )


        // Assertions
        assertEquals(HttpStatus.OK, response.statusCode)

        val updatedSource = accountRepository.findById(sourceAccount.id!!).get()
        val updatedDestination = accountRepository.findById(destinationAccount.id!!).get()

        assertThat(updatedSource.balance).isEqualByComparingTo(BigDecimal(350))
        assertThat(updatedDestination.balance).isEqualByComparingTo(BigDecimal(250))

        val transaction = userTransactionsRepository.findAll().first()
        assertEquals(sourceAccount.id, transaction.sourceId)
        assertEquals(destinationAccount.id, transaction.destinationId)



    }



}