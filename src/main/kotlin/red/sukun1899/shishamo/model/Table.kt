package red.sukun1899.shishamo.model;

/**
 * @author su-kun1899
 */
data class Table(
        val name: String,
        val comment: String,
        val columns: List<Column>,
        val rowSize: Long
)
