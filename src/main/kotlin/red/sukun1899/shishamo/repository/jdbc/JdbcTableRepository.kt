package red.sukun1899.shishamo.repository.jdbc

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import red.sukun1899.shishamo.model.*
import red.sukun1899.shishamo.repository.TableRepository

/**
 *
 * @author yo1000
 */
@Repository
class JdbcTableRepository(
        val jdbcTemplate: NamedParameterJdbcTemplate
) : TableRepository {
    override fun selectAll(schemaName: String): List<TableDetails> {
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

                    TableDetails(
                            name = resultSet.getString("name"),
                            comment = resultSet.getString("comment"),
                            columns = emptyList(),
                            rowSize = resultSet.getLong("rowSize")
                    )
                }
        ) ?: emptyList()
    }

    override fun select(schemaName: String, name: String): TableDetails {
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
                            "name" to resultSet.getString("name"),
                            "comment" to resultSet.getString("comment"),
                            "rowSize" to resultSet.getLong("rowSize"),
                            "columns_name" to resultSet.getString("column_name"),
                            "columns_type" to resultSet.getString("column_type"),
                            "columns_nullable" to resultSet.getBoolean("column_nullable"),
                            "columns_defaultValue" to resultSet.getString("column_defaultValue"),
                            "columns_comment" to resultSet.getString("column_comment"),
                            "columns_parent" to Relation(
                                    table =  Table(resultSet.getString("column_parent_tableName") ?: ""),
                                    column =  Column(resultSet.getString("column_parent_columnName") ?: "")
                            ),
                            "columns_children" to Relation(
                                    table =  Table(resultSet.getString("column_child_tableName") ?: ""),
                                    column =  Column(resultSet.getString("column_child_name") ?: "")
                            )
                    )
                })
                //
                // TODO: 検索結果が存在しない場合のテスト
                //
                .groupBy { mapOf(
                        "name" to it["name"],
                        "comment" to it["comment"],
                        "rowSize" to it["rowSize"]
                ) }
                .map { (key, value) ->
                    TableDetails(
                            name = key["name"] as String,
                            comment = key["comment"] as String,
                            rowSize = key["rowSize"] as Long,
                            columns = value.groupBy { mapOf(
                                    "name" to it["columns_name"],
                                    "type" to it["columns_type"],
                                    "nullable" to it["columns_nullable"],
                                    "defaultValue" to it["columns_defaultValue"],
                                    "comment" to it["columns_comment"],
                                    "parent" to it["columns_parent"]
                            ) }.map { (key, value) -> ColumnDetails(
                                    name = key["name"] as String,
                                    type = key["type"] as String,
                                    nullable = key["nullable"] as Boolean,
                                    defaultValue = key["defaultValue"] as String?,
                                    comment = key["comment"] as String,
                                    parent = key["parent"] as Relation,
                                    children = value.map { it["columns_children"] as Relation }
                            ) }
                    )
                }.first()
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
                SHOW CREATE TABLE ${table.name}
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