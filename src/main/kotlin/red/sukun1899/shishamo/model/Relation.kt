package red.sukun1899.shishamo.model

/**
 * @author yo1000
 */
data class Relation(
        val table: Table,
        val column: Column
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Relation) {
            return false
        }

        return table == other.table && column == other.column
    }

    override fun hashCode(): Int {
        return 4001 xor table.hashCode() xor column.hashCode()
    }
}
