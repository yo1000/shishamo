package red.sukun1899.shishamo.model

/**
 * @author yo1000
 */
class RelationalColumn(
        name: String,
        val type: String,
        val nullable: Boolean,
        val defaultValue: String?,
        val comment: String,
        val parent: Relation,
        val children: List<Relation>
): Column(
        name
)
