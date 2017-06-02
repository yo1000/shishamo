package com.yo1000.shishamo.repository

import com.yo1000.shishamo.model.Index

/**
 * @author su-kun1899
 * @author yo1000
 */
interface IndexRepository {
    fun selectByTableName(schemaName: String, tableName: String): List<Index>
}