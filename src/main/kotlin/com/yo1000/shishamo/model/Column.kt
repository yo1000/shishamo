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

    override fun equals(other: Any?): Boolean {
        if (other !is Column) {
            return false
        }

        return other.name == name
    }

    override fun hashCode(): Int {
        return 4013 xor name.hashCode()
    }
}
