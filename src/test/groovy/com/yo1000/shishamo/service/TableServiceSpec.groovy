package com.yo1000.shishamo.service

import com.yo1000.shishamo.model.Column
import com.yo1000.shishamo.model.ColumnDetails
import com.yo1000.shishamo.model.DataDefinition
import com.yo1000.shishamo.model.Relation
import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.model.TableDetails
import com.yo1000.shishamo.repository.TableRepository
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import com.yo1000.shishamo.model.*
import com.yo1000.shishamo.repository.TableRepository
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author su-kun1899
 */
@Unroll
class TableServiceSpec extends Specification {
    TableService tableService
    DataSourceProperties dataSourceProperties
    TableRepository tableRepository

    def setup() {
        dataSourceProperties = new DataSourceProperties()
        dataSourceProperties.name = 'schema'
        tableRepository = Mock()
        tableService = new TableService(dataSourceProperties, tableRepository)
    }

    def 'Get table list'() {
        given: 'Mocking repository'
        tableRepository.selectAll(_) >> {
            [
                    new Table('table1'),
                    new Table('table2'),
            ]
        }

        when:
        def tables = tableService.get()

        then:
        tables.size() == 2
        tables[0].name == 'table1'
        tables[1].name == 'table2'
    }

    def 'Get table detail'() {
        given: 'Mocking repository'
        def expected = new TableDetails(
                'sample_table',
                '',
                [
                        new ColumnDetails(
                                'columnA', '', false, 'mysql', 'test1',
                                new Relation(new Table(''), new Column('')),
                                Collections.emptyList()),
                        new ColumnDetails(
                                'columnB', '', true, 'oracle', 'test2',
                                new Relation(new Table(''), new Column('')),
                                Collections.emptyList())
                ],
                0L
        )
        tableRepository.select(*_) >> expected

        when:
        def table = tableService.get(expected.name)

        then:
        assert table.name == expected.name
        assert table.columns.size() == expected.columns.size()
        table.columns.eachWithIndex { ColumnDetails column, int i ->
            assert column.name == expected.columns[i].name
            assert column.defaultValue == expected.columns[i].defaultValue
            assert column.nullable == expected.columns[i].nullable
            assert column.comment == expected.columns[i].comment
        }
    }

    def 'Get parent table count'() {
        given:
        tableRepository.selectParentTableCountsByTableName(_) >> {
            [
                    'book'     : new ReferredTable(new Table('book'), 1L),
                    'publisher': new ReferredTable(new Table('publisher'), 0L),
            ]
        }

        when:
        def actual = tableService.parentTableCountsByTableName

        then:
        actual.size() == 1
        actual['book'] == 1L
        actual['publisher'] == null
    }

    def 'Get child table count'() {
        given:
        tableRepository.selectChildTableCountsByTableName(_) >> {
            [
                    'book'     : new ReferredTable(new Table('book'), 1L),
                    'publisher': new ReferredTable(new Table('publisher'), 0L),
            ]
        }

        when:
        def actual = tableService.getChildTableCountsByTableName()

        then:
        actual.size() == 1
        actual['book'] == 1L
        actual['publisher'] == null
    }

    def 'Get column count'() {
        given:
        tableRepository.selectColumnCountsByTableName(_) >> {
            [
                    'book'     : new ReferredTable(new Table('book'), 1L),
                    'publisher': new ReferredTable(new Table('publisher'), 0L),
            ]
        }

        when:
        def actual = tableService.columnCountsByTableName

        then:
        actual.size() == 1
        actual['book'] == 1L
        actual['publisher'] == null
    }

    def 'Get create table statement'() {
        given:
        def createTableStatement = new DataDefinition(new Table('book'), 'Create table book')
        tableRepository.showCreateTableStatement(_) >> createTableStatement

        when:
        def actual = tableService.getCreateTableStatement(new Table('book'))

        then:
        actual.table == createTableStatement.table
        actual.statement == createTableStatement.statement
    }
}
