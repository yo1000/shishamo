package red.sukun1899.shishamo.repository

import red.sukun1899.shishamo.model.DataDefinition
import red.sukun1899.shishamo.model.ReferredTable
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.model.TableDetails

/**
 * @author su-kun1899
 */
interface TableRepository {
    fun selectAll(schemaName: String): List<TableDetails>
    fun select(schemaName: String, name: String): TableDetails
    fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun showCreateTableStatement(table: Table): DataDefinition
}
