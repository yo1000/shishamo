package com.yo1000.shishamo.embedded.mysql

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml

/**
 * @author su-kun1899
 * @author yo1000
 */
@Component
class EmbeddedMysqlComponent(
        val mysql: EmbeddedMysql = INSTANCE_CACHE ?: startEmbeddedMysql(loadConfig())
) {
    private companion object {
        var INSTANCE_CACHE: EmbeddedMysql? = null

        fun loadConfig(): EmbeddedMySqlConfig {
            EmbeddedMysqlComponent::class.java.classLoader
                    .getResourceAsStream("embedded-mysql.yml")?.let {
                return Yaml().loadAs(it, EmbeddedMySqlConfig::class.java)
            }

            // Config file is not found. Use default.
            // FIXME Untested
            return EmbeddedMySqlConfig()
        }

        fun startEmbeddedMysql(config: EmbeddedMySqlConfig): EmbeddedMysql {
            return anEmbeddedMysql(aMysqldConfig(config.version)
                    .withCharset(config.getWixCharset())
                    .withPort(config.port)
                    .withUser(config.username, config.password)
                    .build()
            ).addSchema(config.schemaName)
                    .start()
        }
    }

    init {
        if (INSTANCE_CACHE == null) {
            INSTANCE_CACHE = mysql
        }
    }
}
