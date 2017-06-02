package red.sukun1899.shishamo.service

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import red.sukun1899.shishamo.model.Column
import red.sukun1899.shishamo.model.Index
import red.sukun1899.shishamo.repository.IndexRepository
import spock.lang.Specification
import spock.lang.Unroll
/**
 * @author su-kun1899
 */
@Unroll
class IndexServiceSpec extends Specification {
    IndexService indexService
    DataSourceProperties dataSourceProperties
    IndexRepository indexRepository

    def setup() {
        dataSourceProperties = Mock()
        indexRepository = Mock()
        indexService = new IndexService(dataSourceProperties, indexRepository)
    }

    def 'Get indices by table name'() {
        given: 'Mocking repository'
        dataSourceProperties.name >> 'schema1'
        indexRepository.selectByTableName('schema1', 'table1') >> {
            [
                    new Index(
                            'index1',
                            [new Column('hoge')],
                            Index.Category.PRIMARY
                    ),
                    new Index(
                            'index2',
                            [
                                    new Column('fuga'),
                                    new Column('fuga')
                            ],
                            Index.Category.UNIQUE)
            ]
        }

        when:
        def actual = indexService.get('table1')

        then:
        actual.size() == 2
        actual[0].name == 'index1'
        actual[1].name == 'index2'
    }

    def 'Indices order by category and name'() {
        given: 'Shuffle indices'
        def indices = [
                new Index(
                        'a_pk',
                        [new Column('hoge')],
                        Index.Category.PRIMARY
                ),
                new Index(
                        'b_pk',
                        [new Column('fuga')],
                        Index.Category.PRIMARY
                ),
                new Index(
                        'a_uk',
                        [new Column('piyo')],
                        Index.Category.UNIQUE
                ),
                new Index(
                        'b_uk',
                        [new Column('hogehoge')],
                        Index.Category.UNIQUE
                ),
                new Index(
                        'a_k',
                        [new Column('fugafugafuga')],
                        Index.Category.PERFORMANCE
                ),
                new Index(
                        'b_k',
                        [new Column('piyopiyo')],
                        Index.Category.PERFORMANCE
                )
        ]
        Collections.shuffle(indices)

        and: 'Mocking repository'
        dataSourceProperties.name >> 'schema1'
        indexRepository.selectByTableName('schema1', 'table1') >> indices

        when:
        def actual = indexService.get('table1')

        then:
        actual[0].name == 'a_pk'
        actual[1].name == 'b_pk'
        actual[2].name == 'a_uk'
        actual[3].name == 'b_uk'
        actual[4].name == 'a_k'
        actual[5].name == 'b_k'
    }
}
