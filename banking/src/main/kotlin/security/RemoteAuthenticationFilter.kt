package banking.security


import banking.client.AuthenticationClient
import jakarta.servlet.FilterChain
import jakarta.servlet.http.*
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RemoteAuthenticationFilter(
    private val authenticationClient: AuthenticationClient,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("Remote authentication filter running...")
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        val result = authenticationClient.checkToken(token)
        request.setAttribute("userId", result.userId)

        filterChain.doFilter(request, response)
    }
}

