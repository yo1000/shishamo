package com.yo1000.shishamo.repository.jdbc

import com.yo1000.shishamo.model.Column
import com.yo1000.shishamo.model.Index
import com.yo1000.shishamo.repository.IndexRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * @author yo1000
 */
@Repository
class JdbcIndexRepository(
        val jdbcTemplate: NamedParameterJdbcTemplate
) : IndexRepository {
    private companion object {
        val MAP_KEY_NAME = "name"
        val MAP_KEY_CATEGORY = "category"
        val MAP_KEY_COL_NAME = "column_name"
    }

    override fun selectByTableName(schemaName: String, tableName: String): List<Index> {
        return jdbcTemplate.query("""
                      SELECT
                        idx.index_name AS `name`
                        , CASE
                            WHEN idx.index_name = 'PRIMARY' THEN  idx.index_name
                            WHEN idx.non_unique = 0         THEN  'UNIQUE'
                            ELSE                                  'PERFORMANCE'
                          END AS `category`
                        , idx.column_name AS `column_name`
                      FROM
                        information_schema.statistics idx
                      WHERE
                        idx.table_schema = :schemaName
                        AND
                        idx.table_name = :table
                      ORDER BY
                        idx.table_name
                        , idx.index_name
                        , idx.seq_in_index
                """,
                mapOf(
                        "schemaName" to schemaName,
                        "table" to tableName
                ),
                { resultSet, _ ->
                    mapOf(
                            MAP_KEY_NAME to resultSet.getString("name"),
                            MAP_KEY_CATEGORY to resultSet.getString("category"),
                            MAP_KEY_COL_NAME to resultSet.getString("column_name")
                    )
                })
                .groupBy { it[MAP_KEY_NAME] }
                .map { (key, value) ->
                    if (key == null) {
                        error("Index name is null")
                    }

                    Index(name = key, columns = value.map {
                        Column(it[MAP_KEY_COL_NAME] ?: "")
                    }, category = Index.Category.valueOf(value.first()[MAP_KEY_CATEGORY] ?: ""))
                }
    }
}
