package red.sukun1899.shishamo.repository;

import org.apache.ibatis.annotations.MapKey
import org.apache.ibatis.annotations.Param
import red.sukun1899.shishamo.model.CreateTableStatement
import red.sukun1899.shishamo.model.ReferencedTableCount
import red.sukun1899.shishamo.model.Table

/**
 * @author su-kun1899
 */
interface TableRepository {
    fun selectAll(schemaName: String): List<Table>

    fun select(@Param("schemaName") schemaName: String, @Param("name") name: String): Table

    @MapKey("baseTableName")
    fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>

    @MapKey("baseTableName")
    fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>

    @MapKey("baseTableName")
    fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>

    fun showCreateTableStatement(table: Table): CreateTableStatement
}
