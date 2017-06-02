package red.sukun1899.shishamo.service

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import red.sukun1899.shishamo.model.DataDefinition
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.model.TableDetails
import red.sukun1899.shishamo.repository.TableRepository

/**
 * @author su-kun1899
 */
@Service
class TableService(
        val dataSourceProperties: DataSourceProperties,
        val tableRepository: TableRepository
) {
    @Transactional(readOnly = true)
    fun get(): List<Table> = tableRepository.selectAll(dataSourceProperties.name)

    @Transactional(readOnly = true)
    fun get(tableName: String): TableDetails = tableRepository.select(dataSourceProperties.name, tableName)

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
