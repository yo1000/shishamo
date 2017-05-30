package red.sukun1899.shishamo.model;

/**
 * @author yo1000
 */
class RelationalColumn(
        name: String,
        type: String,
        nullable: Boolean,
        defaultValue: String?,
        comment: String,
        val table: Table,
        val parent: Relation,
        val children: List<Relation>
): Column(
        name,
        type,
        nullable,
        defaultValue,
        comment
)
