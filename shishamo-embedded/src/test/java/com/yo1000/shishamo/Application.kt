package com.yo1000.shishamo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author yo1000
 */
@SpringBootApplication
class Application {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(Application::class.java, *args)
		}
	}
}
