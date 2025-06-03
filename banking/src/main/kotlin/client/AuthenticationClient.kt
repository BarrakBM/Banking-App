package com.bankingapp.banking.client

import jakarta.inject.Named
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Named
class AuthenticationClient(@Value("\${auth.port:8081}")val authPort: Int) {

    fun checkToken(token: String): CheckTokenResponse {
        val restTemplate = RestTemplate()
        val url = "http://localhost:${authPort}/api/auth/check-token"
        val response = restTemplate.exchange<CheckTokenResponse>(
            url = url,
            method = HttpMethod.POST,
            requestEntity = HttpEntity<String>(
                MultiValueMap.fromMultiValue(mapOf("Authorization" to listOf("Bearer $token")))
            ),
            object : ParameterizedTypeReference<CheckTokenResponse>() {
            }
        )
        return response.body ?: throw IllegalStateException("Check token response has no body ...")
    }

}
data class CheckTokenResponse(
    val userId: Long
)