package com.yo1000.shishamo.controller.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import com.yo1000.shishamo.embedded.mysql.EmbeddedMysqlComponent
import spock.lang.Specification
import spock.lang.Unroll
/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class DataSourceControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc
    @Autowired
    EmbeddedMysqlComponent embeddedMysqlComponent
    @Autowired
    DataSourceProperties dataSourceProperties

    def 'Get data source configuration.'() {
        given:
        def url = '/data-source'

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get(url))

        then:
        result.andReturn().modelAndView.modelMap.get('dataSourceProperties') == dataSourceProperties
    }
}
