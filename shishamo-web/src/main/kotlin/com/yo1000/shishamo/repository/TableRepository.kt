package com.yo1000.shishamo.repository

import com.yo1000.shishamo.model.DataDefinition
import com.yo1000.shishamo.model.ReferredTable
import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.model.TableRelation

/**
 * @author su-kun1899
 * @author yo1000
 */
interface TableRepository {
    fun selectAll(schemaName: String): List<TableRelation>
    fun select(schemaName: String, name: String): TableRelation
    fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferredTable>
    fun showCreateTableStatement(table: Table): DataDefinition
}
