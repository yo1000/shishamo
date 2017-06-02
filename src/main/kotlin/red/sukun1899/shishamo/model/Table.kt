package red.sukun1899.shishamo.model

/**
 * @author su-kun1899
 * @author yo1000
 */
open class Table(
        val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Table) {
            return false
        }

        return other.name == name
    }

    override fun hashCode(): Int {
        return 4007 xor name.hashCode()
    }
}
