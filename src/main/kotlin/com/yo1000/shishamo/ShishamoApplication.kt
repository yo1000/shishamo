package com.yo1000.shishamo

import com.yo1000.shishamo.embedded.mysql.EmbeddedMySqlUtil
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author su-kun1899
 * @author yo1000
 */
@SpringBootApplication
class ShishamoApplication {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			if (EmbeddedMySqlUtil.enable()) {
				EmbeddedMySqlUtil.ready()
			}

			SpringApplication.run(ShishamoApplication::class.java, *args)
		}
	}
}
