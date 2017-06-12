package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
class ColumnRelation(
        name: String,
        type: String,
        nullable: Boolean,
        defaultValue: String?,
        comment: String,
        val parent: Relation,
        val children: List<Relation>
) : ColumnDetails(
        name,
        type,
        nullable,
        defaultValue,
        comment
)
