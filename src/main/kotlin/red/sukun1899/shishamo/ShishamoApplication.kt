package red.sukun1899.shishamo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import red.sukun1899.shishamo.embedded.mysql.EmbeddedMySqlUtil

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
