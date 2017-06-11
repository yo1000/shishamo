package com.yo1000.shishamo.model

/**
 *
 * @author yo1000
 */
open class ColumnDetails(
        name: String,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val comment: String
) : Column(name)
