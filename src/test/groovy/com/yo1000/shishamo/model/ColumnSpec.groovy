package com.yo1000.shishamo.model

import spock.lang.Specification

/**
 * @author su-kun1899
 */
class ColumnSpec extends Specification {
    def 'Get index category'() {
        setup:
        def column = new Column(columnName)

        and:
        def indices = [
                new Index('', [new Column('test1'), new Column('test2') ], Index.Category.PRIMARY),
                new Index('', [new Column('test1'), new Column('test3') ], Index.Category.UNIQUE),
                new Index('', [new Column('test4'), new Column('test5') ], Index.Category.PERFORMANCE)
        ]

        expect:
        assert column.getContainedIn(indices).any { it.category == category }

        where:
        columnName || category
        'test1'    || Index.Category.PRIMARY
        'test3'    || Index.Category.UNIQUE
        'test4'    || Index.Category.PERFORMANCE
    }
}
