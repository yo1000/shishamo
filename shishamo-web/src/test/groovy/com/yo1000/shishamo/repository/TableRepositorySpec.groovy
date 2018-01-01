package com.yo1000.shishamo.repository

import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.destination.DataSourceDestination
import com.yo1000.shishamo.embedded.mysql.EmbeddedMysqlComponent
import com.yo1000.shishamo.model.Table
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static com.ninja_squad.dbsetup.Operations.*
/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
class TableRepositorySpec extends Specification {
    @Autowired
    EmbeddedMysqlComponent embeddedMysqlComponent
    @Autowired
    TableRepository tableRepository
    @Autowired
    DataSourceProperties dataSourceProperties
    @Autowired
    DataSourceDestination destination

    def 'Get tables'() {
        given: 'Prepare test tables.'
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS book'),
                sql("""
                    CREATE TABLE `book` (
                        `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                        `title` varchar(128) NOT NULL COMMENT 'タイトル',
                        `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                        PRIMARY KEY (`isbn`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                    """),
                insertInto('book')
                        .columns("isbn", "title", "publisherid")
                        .values(123, "FRA", 1)
                        .values(456, "USA", 2)
                        .build(),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def tables = tableRepository.selectAll(dataSourceProperties.name)

        then:
        tables.size() == 1
        tables.each {
            assert it.getName() == 'book'
            assert it.getComment() == '書籍'
            assert it.getRowSize() == 2L
        }
    }

    def 'Get table'() {
        // FIXME untested defaultValue, nullable
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book_notes`'),
                sql("""
                    CREATE TABLE `book_notes` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `note` varchar(40) NOT NULL COMMENT '備考',
                      PRIMARY KEY (`isbn`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍備考'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher`  (`publisherid`),
                      CONSTRAINT `isbn`        FOREIGN KEY (`isbn`)        REFERENCES `book_notes` (`isbn`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                insertInto('book')
                        .columns('isbn', 'title', 'publisherid', 'author')
                        .values(123, 'FRA', 1, 'zidane')
                        .values(456, 'USA', 2, 'donoban')
                        .build(),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def table = tableRepository.select(dataSourceProperties.name, tableName)

        then:
        table.getName() == tableName
        table.getComment() == '書籍'
        table.getColumns().size() == 4
        table.getRowSize() == 2L

        and:
        table.getColumns().get(0).getName() == 'isbn'
        table.getColumns().get(0).getType() == 'bigint(19)'
        table.getColumns().get(0).getDefaultValue() == null
        table.getColumns().get(0).getComment() == 'ISBN'
        assert !table.getColumns().get(0).getNullable()
        table.columns[0].parent.table.name == 'book_notes'

        and:
        table.getColumns().get(1).getName() == 'title'
        table.getColumns().get(1).getType() == 'varchar(128)'
        table.getColumns().get(1).getDefaultValue() == null
        table.getColumns().get(1).getComment() == 'タイトル'
        assert !table.getColumns().get(1).getNullable()

        and:
        table.getColumns().get(2).getName() == 'publisherid'
        table.getColumns().get(2).getType() == 'int(10) unsigned'
        table.getColumns().get(2).getDefaultValue() == null
        table.getColumns().get(2).getComment() == '出版社ID'
        assert !table.getColumns().get(2).getNullable()
        table.columns[2].parent.table.name == 'publisher'

        and:
        table.getColumns().get(3).getName() == 'author'
        table.getColumns().get(3).getType() == 'varchar(40)'
        table.getColumns().get(3).getDefaultValue() == null
        table.getColumns().get(3).getComment() == '著者'
        assert !table.getColumns().get(3).getNullable()

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `book_notes`'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('SET foreign_key_checks = 1')
        )).launch()

        where:
        tableName | _
        'book'    | _
    }

    def 'Get child column'() {
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('DROP TABLE IF EXISTS `book2`'),
                sql("""
                    CREATE TABLE `book2` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_2` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def table = tableRepository.select(dataSourceProperties.name, tableName)

        then:
        table.name == tableName
        table.columns[0].name == 'publisherid'
        table.columns[0].children.size() == 2
        table.columns[0].children[0].table.name == 'book'
        table.columns[0].children[1].table.name == 'book2'

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('DROP TABLE IF EXISTS `book2`'),
                sql('SET foreign_key_checks = 1')
        )).launch()

        where:
        tableName   | _
        'publisher' | _
    }


    def 'Get parent table count'() {
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def actual = tableRepository.selectParentTableCountsByTableName(dataSourceProperties.name)

        then:
        actual.size() == 2
        actual.get('publisher').getReferences() == 0L
        actual.get('book').getReferences() == 1L

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('SET foreign_key_checks = 1')
        )).launch()
    }

    def 'Get child table count'() {
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('DROP TABLE IF EXISTS `publisher2`'),
                sql("""
                    CREATE TABLE `publisher2` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book2`'),
                sql("""
                    CREATE TABLE `book2` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_2` FOREIGN KEY (`publisherid`) REFERENCES `publisher2` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('DROP TABLE IF EXISTS `book3`'),
                sql("""
                    CREATE TABLE `book3` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_3` FOREIGN KEY (`publisherid`) REFERENCES `publisher2` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def actual = tableRepository.selectChildTableCountsByTableName(dataSourceProperties.name)

        then:
        actual.size() == 2
        actual.get('publisher').getReferences() == 1L
        actual.get('publisher2').getReferences() == 2L

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `publisher2`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('DROP TABLE IF EXISTS `book2`'),
                sql('DROP TABLE IF EXISTS `book3`'),
                sql('SET foreign_key_checks = 1')
        )).launch()
    }

    def 'Get column count'() {
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def actual = tableRepository.selectColumnCountsByTableName(dataSourceProperties.name)

        then:
        actual.size() == 2
        actual.get('publisher').getReferences() == 2L
        actual.get('book').getReferences() == 4L

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('SET foreign_key_checks = 1')
        )).launch()
    }

    def 'Show create table.'() {
        given:
        new DbSetup(destination, sequenceOf(
                sql('SET NAMES utf8'),
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql("""
                    CREATE TABLE `publisher` (
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `name` varchar(40) NOT NULL COMMENT '出版社名',
                      PRIMARY KEY (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出版社'
                """),
                sql('DROP TABLE IF EXISTS `book`'),
                sql("""
                    CREATE TABLE `book` (
                      `isbn` bigint(19) NOT NULL COMMENT 'ISBN',
                      `title` varchar(128) NOT NULL COMMENT 'タイトル',
                      `publisherid` int(10) unsigned NOT NULL COMMENT '出版社ID',
                      `author` varchar(40) NOT NULL COMMENT '著者',
                      PRIMARY KEY (`isbn`),
                      KEY `publisherid` (`publisherid`),
                      CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='書籍'
                """),
                sql('SET foreign_key_checks = 1')
        )).launch()

        when:
        def actual = tableRepository.showCreateTableStatement(new Table('book'))

        then:
        actual.table.name == 'book'
        actual.statement == 'CREATE TABLE `book` (\n' +
                '  `isbn` bigint(19) NOT NULL COMMENT \'ISBN\',\n' +
                '  `title` varchar(128) NOT NULL COMMENT \'タイトル\',\n' +
                '  `publisherid` int(10) unsigned NOT NULL COMMENT \'出版社ID\',\n' +
                '  `author` varchar(40) NOT NULL COMMENT \'著者\',\n' +
                '  PRIMARY KEY (`isbn`),\n' +
                '  KEY `publisherid` (`publisherid`),\n' +
                '  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`publisherid`) REFERENCES `publisher` (`publisherid`)\n' +
                ') ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT=\'書籍\''

        cleanup:
        new DbSetup(destination, sequenceOf(
                sql('SET foreign_key_checks = 0'),
                sql('DROP TABLE IF EXISTS `publisher`'),
                sql('DROP TABLE IF EXISTS `book`'),
                sql('SET foreign_key_checks = 1')
        )).launch()
    }
}
