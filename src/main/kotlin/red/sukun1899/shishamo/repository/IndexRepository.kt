package red.sukun1899.shishamo.repository

import red.sukun1899.shishamo.model.Index

/**
 * @author su-kun1899
 * @author yo1000
 */
interface IndexRepository {
    fun selectByTableName(schemaName: String, tableName: String): List<Index>
}