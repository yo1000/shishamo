package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class TableSearchResult(
        name: String,
        comment: String,
        rowSize: Long,
        val columns: List<ColumnDetails>
) : TableDetails(
        name,
        comment,
        rowSize
)
