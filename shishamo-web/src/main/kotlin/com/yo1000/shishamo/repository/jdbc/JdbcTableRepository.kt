package com.yo1000.shishamo.repository.jdbc

import com.yo1000.shishamo.model.*
import com.yo1000.shishamo.repository.TableRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * @author yo1000
 */
@Repository
class JdbcTableRepository(
        val jdbcTemplate: NamedParameterJdbcTemplate
) : TableRepository {
    private companion object {
        val MAP_KEY_NAME = "name"
        val MAP_KEY_COMMENT = "comment"
        val MAP_KEY_ROW_SIZE = "rowSize"
        val MAP_KEY_COLS_NAME = "columns_name"
        val MAP_KEY_COLS_TYPE = "columns_type"
        val MAP_KEY_COLS_NULLABLE = "columns_nullable"
        val MAP_KEY_COLS_DEFAULT_VALUE = "columns_defaultValue"
        val MAP_KEY_COLS_COMMENT = "columns_comment"
        val MAP_KEY_COLS_PARENT = "columns_parent"
        val MAP_KEY_COLS_CHILDREN = "columns_children"
        val MAP_KEY_TABLE = "table"
        val MAP_KEY_COLUMN = "column"
    }

    override fun selectAll(schemaName: String): List<TableRelation> {
        return jdbcTemplate.query("""
                SELECT
                    tab.table_name    AS `name`,
                    tab.table_comment AS `comment`,
                    tab.table_rows    AS `rowSize`
                FROM
                    information_schema.tables tab
                WHERE
                    tab.table_schema = :schemaName
                    AND
                    tab.table_type = 'BASE TABLE'
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->

                    TableRelation(
                            name = resultSet.getString("name"),
                            comment = resultSet.getString("comment"),
                            columns = emptyList(),
                            rowSize = resultSet.getLong("rowSize")
                    )
                }
        ) ?: emptyList()
    }

    override fun select(schemaName: String, name: String): TableRelation {
        return jdbcTemplate.query("""
                SELECT
                    tab.table_name                AS `name`,
                    tab.table_comment             AS `comment`,
                    tab.table_rows                AS `rowSize`,
                    col.column_name               AS `column_name`,
                    col.column_type               AS `column_type`,
                    col.column_default            AS `column_defaultValue`,
                    col.is_nullable               AS `column_nullable`,
                    col.column_comment            AS `column_comment`,
                    parent.referenced_table_name  AS `column_parent_tableName`,
                    parent.referenced_column_name AS `column_parent_columnName`,
                    child.column_name             AS `column_child_name`,
                    child.table_name              AS `column_child_tableName`
                FROM
                    information_schema.tables tab
                    INNER JOIN
                    information_schema.columns col
                        ON tab.table_schema = col.table_schema AND tab.table_name = col.table_name
                    LEFT OUTER JOIN
                    information_schema.key_column_usage parent
                        ON col.table_schema = parent.table_schema
                           AND col.table_name = parent.table_name
                           AND col.column_name = parent.column_name
                    LEFT OUTER JOIN
                    information_schema.key_column_usage child
                        ON col.table_schema = child.table_schema
                           AND col.table_name = child.referenced_table_name
                           AND col.column_name = child.referenced_column_name
                WHERE
                    tab.table_schema = :schemaName
                    AND
                    tab.table_name = :name
                    AND
                    tab.table_type = 'BASE TABLE'
                ORDER BY
                    col.ordinal_position
                """,
                mapOf(
                        "schemaName" to schemaName,
                        "name" to name
                ),
                { resultSet, _ ->
                    mapOf(
                            MAP_KEY_NAME to resultSet.getString("name"),
                            MAP_KEY_COMMENT to resultSet.getString("comment"),
                            MAP_KEY_ROW_SIZE to resultSet.getLong("rowSize"),
                            MAP_KEY_COLS_NAME to resultSet.getString("column_name"),
                            MAP_KEY_COLS_TYPE to resultSet.getString("column_type"),
                            MAP_KEY_COLS_NULLABLE to resultSet.getBoolean("column_nullable"),
                            MAP_KEY_COLS_DEFAULT_VALUE to resultSet.getString("column_defaultValue"),
                            MAP_KEY_COLS_COMMENT to resultSet.getString("column_comment"),
                            MAP_KEY_COLS_PARENT to mapOf(
                                    MAP_KEY_TABLE to (resultSet.getString("column_parent_tableName") ?: ""),
                                    MAP_KEY_COLUMN to (resultSet.getString("column_parent_columnName") ?: "")
                            ),
                            MAP_KEY_COLS_CHILDREN to mapOf(
                                    MAP_KEY_TABLE to (resultSet.getString("column_child_tableName") ?: ""),
                                    MAP_KEY_COLUMN to (resultSet.getString("column_child_name") ?: "")
                            )
                    )
                })
                .groupBy { mapOf(
                        MAP_KEY_NAME to it[MAP_KEY_NAME],
                        MAP_KEY_COMMENT to it[MAP_KEY_COMMENT],
                        MAP_KEY_ROW_SIZE to it[MAP_KEY_ROW_SIZE]
                ) }
                .map { (key, value) ->
                    TableRelation(
                            name = key[MAP_KEY_NAME] as String,
                            comment = key[MAP_KEY_COMMENT] as String,
                            rowSize = key[MAP_KEY_ROW_SIZE] as Long,
                            columns = value.groupBy { mapOf(
                                    MAP_KEY_COLS_NAME to it[MAP_KEY_COLS_NAME],
                                    MAP_KEY_COLS_TYPE to it[MAP_KEY_COLS_TYPE],
                                    MAP_KEY_COLS_NULLABLE to it[MAP_KEY_COLS_NULLABLE],
                                    MAP_KEY_COLS_DEFAULT_VALUE to it[MAP_KEY_COLS_DEFAULT_VALUE],
                                    MAP_KEY_COLS_COMMENT to it[MAP_KEY_COLS_COMMENT]
                            ) }.map { (key, value) ->
                                val parent: Relation = value.map {
                                    it[MAP_KEY_COLS_PARENT] as Map<*, *>?
                                }.map {
                                    it?.let {
                                        Relation(
                                                table = Table(it[MAP_KEY_TABLE] as String),
                                                column = Column(it[MAP_KEY_COLUMN] as String)
                                        )
                                    }
                                }.filterNotNull().firstOrNull()?: Relation(Table(""), Column(""))

                                val children: List<Relation> = value.map {
                                    it[MAP_KEY_COLS_CHILDREN] as Map<*, *>?
                                }.map {
                                    it?.let {
                                        Relation(
                                                table = Table(it[MAP_KEY_TABLE] as String),
                                                column = Column(it[MAP_KEY_COLUMN] as String)
                                        )
                                    }
                                }.filterNotNull()

                                ColumnRelation(
                                        name = key[MAP_KEY_COLS_NAME] as String,
                                        type = key[MAP_KEY_COLS_TYPE] as String,
                                        nullable = key[MAP_KEY_COLS_NULLABLE] as Boolean,
                                        defaultValue = key[MAP_KEY_COLS_DEFAULT_VALUE] as String?,
                                        comment = key[MAP_KEY_COLS_COMMENT] as String,
                                        parent = parent,
                                        children = children
                                )
                            }
                    )
                }.first()
    }

    override fun selectByQueries(schemaName: String, queries: List<String>): List<TableSearchResult> {
        return jdbcTemplate.query("""
                SELECT DISTINCT
                    tbl.table_name      AS `name`,
                    tbl.table_comment   AS `comment`,
                    tbl.table_rows      AS `rowSize`,
                    col.column_name     AS `column_name`,
                    col.column_type     AS `column_type`,
                    col.column_default  AS `column_defaultValue`,
                    col.is_nullable     AS `column_nullable`,
                    col.column_comment  AS `column_comment`,
                    col.ordinal_position
                FROM
                    (
                        SELECT
                            tbl_1.table_name,
                            tbl_1.table_comment,
                            tbl_1.table_rows,
                            tbl_1.table_schema
                        FROM
                            information_schema.tables tbl_1
                        INNER JOIN
                            information_schema.columns col_1
                            ON  tbl_1.table_schema  = :schemaName
                            AND tbl_1.table_schema  = col_1.table_schema
                            AND tbl_1.table_name    = col_1.table_name
                        WHERE
                                tbl_1.table_type = 'BASE TABLE'
                            AND ( ${(1..queries.size).map { """
                                    tbl_1.table_name        LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                                OR  tbl_1.table_comment     LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                                OR  col_1.column_name       LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                                OR  col_1.column_comment    LIKE CONCAT(CONCAT('%', :keyword_$it), '%')
                            """ }.joinToString(separator = " OR ")} )
                    ) tbl
                INNER JOIN
                    information_schema.columns col
                    ON  tbl.table_schema    = col.table_schema
                    AND tbl.table_name      = col.table_name
                ORDER BY
                    col.ordinal_position
                """,
                (1..queries.size).map {
                    "keyword_$it" to queries[it - 1]
                }.plus("schemaName" to schemaName).toMap(),
                { resultSet, _ ->
                    mapOf(
                            MAP_KEY_NAME to resultSet.getString("name"),
                            MAP_KEY_COMMENT to resultSet.getString("comment"),
                            MAP_KEY_ROW_SIZE to resultSet.getLong("rowSize"),
                            MAP_KEY_COLS_NAME to resultSet.getString("column_name"),
                            MAP_KEY_COLS_TYPE to resultSet.getString("column_type"),
                            MAP_KEY_COLS_NULLABLE to resultSet.getBoolean("column_nullable"),
                            MAP_KEY_COLS_DEFAULT_VALUE to resultSet.getString("column_defaultValue"),
                            MAP_KEY_COLS_COMMENT to resultSet.getString("column_comment")
                    )
                })
                .groupBy { mapOf(
                        MAP_KEY_NAME to it[MAP_KEY_NAME],
                        MAP_KEY_COMMENT to it[MAP_KEY_COMMENT],
                        MAP_KEY_ROW_SIZE to it[MAP_KEY_ROW_SIZE]
                ) }
                .map { (key, value) ->
                    TableSearchResult(
                            name = key[MAP_KEY_NAME] as String,
                            comment = key[MAP_KEY_COMMENT] as String,
                            rowSize = key[MAP_KEY_ROW_SIZE] as Long,
                            columns = value.map {
                                ColumnDetails(
                                        name = it[MAP_KEY_COLS_NAME] as String,
                                        type = it[MAP_KEY_COLS_TYPE] as String,
                                        nullable = it[MAP_KEY_COLS_NULLABLE] as Boolean,
                                        defaultValue = it[MAP_KEY_COLS_DEFAULT_VALUE] as String?,
                                        comment = it[MAP_KEY_COLS_COMMENT] as String
                                )
                            },
                            queries = queries
                    )
                }
    }

    override fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferredTable> {
        return jdbcTemplate.query("""
                SELECT
                    table_name     AS "table",
                    sum(col_count) AS "references"
                FROM
                    (
                        SELECT
                            table_name,
                            count(referenced_column_name) AS col_count
                        FROM
                            information_schema.key_column_usage
                        WHERE
                            table_schema = :schemaName
                        GROUP BY
                            table_name, referenced_table_name
                    ) parent_col
                GROUP BY
                    table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferredTable(
                            table = Table(resultSet.getString("table")),
                            references = resultSet.getLong("references"))
                })
                .associateBy { it.table.name }
    }

    override fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferredTable> {
        return jdbcTemplate.query("""
                SELECT
                    table_name AS "table",
                    sum(count) AS "references"
                FROM
                    (
                        SELECT
                            referenced_table_name AS table_name,
                            count(column_name)    AS count
                        FROM
                            information_schema.key_column_usage
                        WHERE
                            table_schema = :schemaName
                            AND
                            referenced_table_name IS NOT NULL
                        GROUP BY
                            referenced_table_name, table_name
                    ) child_col
                GROUP BY
                    table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferredTable(
                            table = Table(resultSet.getString("table")),
                            references = resultSet.getLong("references"))
                })
                .associateBy { it.table.name }
    }

    override fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferredTable> {
        return jdbcTemplate.query("""
                SELECT
                    tbl.table_name              AS "table",
                    count(col.column_name) AS "references"
                FROM
                    information_schema.tables tbl
                INNER JOIN
                    information_schema.columns col
                        ON  tbl.table_schema  = col.table_schema
                        AND tbl.table_name    = col.table_name
                WHERE
                    tbl.table_schema  = :schemaName
                AND tbl.table_type    = 'BASE TABLE'
                GROUP BY
                    tbl.table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferredTable(
                            table = Table(resultSet.getString("table")),
                            references = resultSet.getLong("references"))
                })
                .associateBy { it.table.name }
    }

    override fun showCreateTableStatement(table: Table): DataDefinition {
        return jdbcTemplate.query("""
                SHOW CREATE TABLE `${table.name}`
                """,
                { resultSet, _ ->
                    DataDefinition(
                            table = Table(resultSet.getString("table")),
                            statement = resultSet.getString("create table")
                    )
                }
        ).first() ?: DataDefinition(Table(""), "")
    }
}