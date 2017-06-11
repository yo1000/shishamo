package com.yo1000.shishamo.service

import com.yo1000.shishamo.model.DataDefinition
import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.model.TableRelation
import com.yo1000.shishamo.repository.TableRepository
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author su-kun1899
 * @author yo1000
 */
@Service
class TableService(
        val dataSourceProperties: DataSourceProperties,
        val tableRepository: TableRepository
) {
    @Transactional(readOnly = true)
    fun get(): List<Table> = tableRepository.selectAll(dataSourceProperties.name)

    @Transactional(readOnly = true)
    fun get(tableName: String): TableRelation = tableRepository.select(dataSourceProperties.name, tableName)

    @Transactional(readOnly = true)
    fun getCreateTableStatement(table: Table): DataDefinition = tableRepository.showCreateTableStatement(table)

    /**
     * @return Key: table, Value: Parent table's references
     */
    fun getParentTableCountsByTableName(): Map<String, Long> = tableRepository
            .selectParentTableCountsByTableName(dataSourceProperties.name)
            .filter { it.value.references > 0 }
            .map { it.key to it.value.references }
            .toMap()

    /**
     * @return Key: table, Value: Child table's references
     */
    fun getChildTableCountsByTableName(): Map<String, Long> = tableRepository
            .selectChildTableCountsByTableName(dataSourceProperties.name)
            .filter { it.value.references > 0 }
            .map { it.key to it.value.references }
            .toMap()

    /**
     * @return Key: table, Value: Column references
     */
    fun getColumnCountsByTableName(): Map<String, Long> = tableRepository
            .selectColumnCountsByTableName(dataSourceProperties.name)
            .filter { it.value.references > 0 }
            .map { it.key to it.value.references }
            .toMap()
}
