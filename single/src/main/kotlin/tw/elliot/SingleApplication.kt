package tw.elliot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SingleApplication

fun main(args: Array<String>) {
    runApplication<SingleApplication>(*args)
}
