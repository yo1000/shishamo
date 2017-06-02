package com.yo1000.shishamo.model

import spock.lang.Specification

/**
 * @author su-kun1899
 */
class ColumnSpec extends Specification {
    def 'Get index category'() {
        setup:
        def column = makeColumn(columnName)

        and:
        def indices = [
                makeIndex(Index.Category.PRIMARY, [makeColumn('test1'), makeColumn('test2') ]),
                makeIndex(Index.Category.UNIQUE, [makeColumn('test1'), makeColumn('test3') ]),
                makeIndex(Index.Category.PERFORMANCE, [makeColumn('test4'), makeColumn('test5') ])
        ]

        expect:
        column.getIndexCategory(indices) == category

        where:
        columnName || category
        'test1'    || Index.Category.PRIMARY
        'test3'    || Index.Category.UNIQUE
        'test4'    || Index.Category.PERFORMANCE
        'test99'   || null
    }

    def makeColumn(String name) {
        return new Column(name)
    }

    def makeIndex(Index.Category category, List<Column> columns) {
        return new Index(
                '',
                columns,
                category
        )
    }
}
