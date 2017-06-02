package red.sukun1899.shishamo.controller.api.v1

import org.hamcrest.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import red.sukun1899.shishamo.embedded.mysql.EmbeddedMySqlUtil
import red.sukun1899.shishamo.model.*
import red.sukun1899.shishamo.service.TableService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class TablesRestControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpyBean
    TableService tableService

    def setupSpec() {
        if (EmbeddedMySqlUtil.enable()){
            EmbeddedMySqlUtil.ready()
        }
    }

    def 'Get table list'() {
        setup: 'Prepare expected value'
        def tables = [
                new Table('table1'),
                new Table('table2'),
        ]

        and: 'Mocking service'
        Mockito.doReturn(tables).when(tableService).get()

        and: 'URL'
        def url = '/v1/tables'

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$', Matchers.hasSize(tables.size())))
                .andExpect(jsonPath('$[0].name').value(tables[0].getName()))
                .andExpect(jsonPath('$[1].name').value(tables[1].getName()))
    }

    def 'Get table detail'() {
        setup: 'Mock service'
        def table = new TableDetails(
                tableName, "",
                [makeColumn(columnNames[0]), makeColumn(columnNames[1])],
                0L
        )
        Mockito.doReturn(table).when(tableService).get(Mockito.anyString())

        and: 'URL'
        def url = '/v1/tables/' + tableName

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$.name').value(table.name))
                .andExpect(jsonPath('$.columns').isArray())
                .andExpect(jsonPath('$.columns', Matchers.hasSize(table.columns.size())))
                .andExpect(jsonPath('$.columns[0].name').value(table.columns[0].name))
                .andExpect(jsonPath('$.columns[1].name').value(table.columns[1].name))

        where:
        tableName      | columnNames
        'sample_table' | ['columnA', 'columnB']
    }

    def 'Get column detail'() {
        setup: 'Prepare expected value'
        def table = new TableDetails(
                'sample_table', '',
                [new ColumnDetails(
                        name,
                        type,
                        nullable,
                        defaultValue,
                        comment,
                        new Relation(new Table(''), new Column('')),
                        Collections.emptyList()
                )],
                0L
        )

        and: 'URL'
        def url = '/v1/tables/' + table.getName()

        and: 'Mocking service'
        Mockito.doReturn(table).when(tableService).get(table.getName())

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$.columns[0].name').value(table.columns[0].name))
                .andExpect(jsonPath('$.columns[0].defaultValue').value(table.columns[0].defaultValue))
                .andExpect(jsonPath('$.columns[0].nullable').value(table.columns[0].nullable))
                .andExpect(jsonPath('$.columns[0].type').value(table.columns[0].type))
                .andExpect(jsonPath('$.columns[0].comment').value(table.columns[0].comment))

        where:
        name      | defaultValue     | nullable | type          | comment
        'columnA' | 'default_sample' | true     | 'varchar(40)' | 'comment_sample'
    }

    def makeColumn(String name) {
        return new ColumnDetails(
                name, '', false, null, '',
                new Relation(new Table(''), new Column('')),
                Collections.emptyList()
        )
    }
}
