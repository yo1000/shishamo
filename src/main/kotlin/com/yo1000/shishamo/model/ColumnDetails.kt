package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class ColumnDetails(
        name: String,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val comment: String,
        val parent: Relation,
        val children: List<Relation>
) : Column(
        name
)
