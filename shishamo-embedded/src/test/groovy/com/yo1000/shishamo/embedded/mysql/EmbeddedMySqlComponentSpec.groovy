package com.yo1000.shishamo.embedded.mysql

import com.wix.mysql.config.Charset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static com.wix.mysql.distribution.Version.v5_7_latest

/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
class EmbeddedMySqlComponentSpec extends Specification {
    @Autowired
    EmbeddedMysqlComponent embeddedMysqlComponent

    def 'Get configuration'() {
        expect:
        embeddedMysqlComponent.mysql.config.version == v5_7_latest
        embeddedMysqlComponent.mysql.config.charset == Charset.UTF8
        embeddedMysqlComponent.mysql.config.port == 12215
        embeddedMysqlComponent.mysql.config.username == 'demo_user'
        embeddedMysqlComponent.mysql.config.password == 'demo_password'
    }
}
