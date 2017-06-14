package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class TableSearchResult(
        name: String,
        comment: String,
        rowSize: Long,
        val columns: List<ColumnDetails>,
        val queries: List<String>
) : TableDetails(
        name,
        comment,
        rowSize
) {
    fun getNameHit(): Boolean {
        return queries.any { name.contains(it) || comment.contains(it) }

    }

    fun getHitColumns(): List<ColumnDetails> {
        return columns.filter {
            val col: ColumnDetails = it
            queries.any { col.name.contains(it) || col.comment.contains(it) }
        }
    }

    fun getMishitColumns(): List<ColumnDetails> {
        return columns.filter {
            val col: ColumnDetails = it
            !queries.any { col.name.contains(it) || col.comment.contains(it) }
        }
    }
}
