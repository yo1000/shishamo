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
                            "name" to resultSet.getString("name"),
                            "category" to resultSet.getString("category"),
                            "columns_name" to resultSet.getString("column_name")
                    )
                })
                .groupBy { it["name"] }
                .map { (key, value) ->
                    if (key == null) {
                        error("Index name is null")
                    }

                    Index(name = key, columns = value.map {
                        Column(it["columns_name"] ?: "")
                    }, category = Index.Category.valueOf(value.first()["category"] ?: ""))
                }
    }
}
