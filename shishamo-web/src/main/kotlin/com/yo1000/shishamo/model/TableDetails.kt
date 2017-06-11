package com.yo1000.shishamo.model

/**
 * @author su-kun1899
 * @author yo1000
 */
open class TableDetails(
        name: String,
        val comment: String,
        val rowSize: Long
) : Table(
        name
)

