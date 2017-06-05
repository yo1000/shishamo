package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class TableDetails(
        name: String,
        val comment: String,
        val columns: List<ColumnDetails>,
        val rowSize: Long
) : Table(
        name
)
