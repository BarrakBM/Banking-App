package authentication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Authentication

fun main(args: Array<String>) {
    runApplication<Authentication>(*args)
}
