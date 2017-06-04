package com.yo1000.shishamo.model

/**
 * @author su-kun1899
 * @author yo1000
 */
data class Index(
        val name: String,
        val columns: List<Column>,
        val category: Category
) {
    enum class Category(
            val order: Int
    ) {
        NONE(0),
        PRIMARY(10),
        UNIQUE(20),
        PERFORMANCE(30)
    }

    fun getPrimary(): Boolean = category == Category.PRIMARY
    fun getUnique(): Boolean = category == Category.UNIQUE
    fun getPerformance(): Boolean = category == Category.PERFORMANCE
}
