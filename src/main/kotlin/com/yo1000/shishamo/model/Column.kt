package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
open class Column(
        val name: String
) {
    fun getIndexCategory(indices: Collection<Index>): Index.Category? {
        return indices.filter {
            it.columns.any {
                it.name == this.name
            }
        }.map { it.category }.firstOrNull()
    }
}
