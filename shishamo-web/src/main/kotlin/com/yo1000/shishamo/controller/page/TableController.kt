package com.yo1000.shishamo.controller.page

import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.model.TableDetails
import com.yo1000.shishamo.service.IndexService
import com.yo1000.shishamo.service.TableService
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author su-kun1899
 * @author yo1000
 */
@Controller
@RequestMapping("tables")
class TableController(
        val dataSourceProperties: DataSourceProperties,
        val tableService: TableService,
        val indexService: IndexService
) {
    @GetMapping
    fun get(model: Model): String {
        val tables: List<Table> = tableService.get()
        model.addAttribute("tables", tables)

        model.addAttribute("parentTableCounts", tableService.getParentTableCountsByTableName())
        model.addAttribute("childTableCounts", tableService.getChildTableCountsByTableName())
        model.addAttribute("columnCounts", tableService.getColumnCountsByTableName())
        model.addAttribute("schemaName", dataSourceProperties.name)

        return "tables"
    }

    @GetMapping(path = arrayOf("{tableName}"))
    fun get(@PathVariable tableName: String, model: Model): String {
        val table: TableDetails = tableService.get(tableName)

        model.addAttribute("table", table)
        model.addAttribute("createTableStatement", tableService.getCreateTableStatement(table))
        model.addAttribute("indices", indexService.get(tableName))
        model.addAttribute("schemaName", dataSourceProperties.name)

        return "table"
    }
}
