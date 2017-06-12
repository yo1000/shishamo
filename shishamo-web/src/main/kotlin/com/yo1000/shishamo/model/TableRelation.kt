package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class TableRelation(
        name: String,
        comment: String,
        rowSize: Long,
        val columns: List<ColumnRelation>
) : TableDetails(
        name,
        comment,
        rowSize
)
