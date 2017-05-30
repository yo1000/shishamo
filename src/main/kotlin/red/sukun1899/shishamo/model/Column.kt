package red.sukun1899.shishamo.model;

/**
 * @author su-kun1899
 */
open class Column(
        val name: String,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val comment: String
) {
    fun getIndexCategory(indices: Collection<Index>): Index.Category? {
        return indices.filter {
            it.columns.any {
                it.name == this.name
            }
        }.map { it.category }.firstOrNull()
    }
}
