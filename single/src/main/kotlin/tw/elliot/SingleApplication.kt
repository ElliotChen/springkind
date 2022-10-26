package tw.elliot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
class SingleApplication

fun main(args: Array<String>) {
	runApplication<SingleApplication>(*args)
}
