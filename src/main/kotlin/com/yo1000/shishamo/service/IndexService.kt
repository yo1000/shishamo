package com.yo1000.shishamo.service

import com.yo1000.shishamo.model.Index
import com.yo1000.shishamo.repository.IndexRepository
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author su-kun1899
 * @author yo1000
 */
@Service
class IndexService(
        val dataSourceProperties: DataSourceProperties,
        val indexRepository: IndexRepository
) {
    @Transactional(readOnly = true)
    fun get(tableName: String): List<Index> = indexRepository.selectByTableName(dataSourceProperties.name, tableName)
            .sortedWith(Comparator { (name1, _, category1), (name2, _, category2) ->
                if (category1.order != category2.order) category1.order - category2.order
                else name1.compareTo(name2)
            })
}
