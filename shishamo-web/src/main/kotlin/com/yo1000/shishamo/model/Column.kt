package com.yo1000.shishamo.model

/**
 * @author yo1000
 */
open class Column(
        val name: String
) {
    fun getContainedIn(indices: Collection<Index>): List<Index> {
        return indices.filter {
            it.columns.any {
                it.name == this.name
            }
        }
    }

    fun indexesPrimary(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.PRIMARY }
    }

    fun indexesUnique(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.UNIQUE }
    }

    fun indexesPerformance(indices: Collection<Index>): Boolean {
        return getContainedIn(indices).any { it.category == Index.Category.PERFORMANCE }
    }
}
