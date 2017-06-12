package com.yo1000.shishamo.controller.page

import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.model.TableRelation
import com.yo1000.shishamo.model.TableSearchResult
import com.yo1000.shishamo.service.IndexService
import com.yo1000.shishamo.service.TableService
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

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
        model.addAttribute("schemaName", dataSourceProperties.name)
        model.addAttribute("tables", tables)

        model.addAttribute("parentTableCounts", tableService.getParentTableCountsByTableName())
        model.addAttribute("childTableCounts", tableService.getChildTableCountsByTableName())
        model.addAttribute("columnCounts", tableService.getColumnCountsByTableName())

        return "tables"
    }

    @GetMapping(path = arrayOf("search"))
    fun getByKeyword(@RequestParam keyword: String, model: Model): String {
        val keywords: List<String> = keyword.split(Regex("""[\s　]+"""))
        val tables: List<TableSearchResult> = tableService.getTablesByKeyword(keywords)
        model.addAttribute("keywords", keywords)
        model.addAttribute("tables", tables)

        return "search"
    }

    @GetMapping(path = arrayOf("{tableName}"))
    fun get(@PathVariable tableName: String, model: Model): String {
        val table: TableRelation = tableService.get(tableName)

        model.addAttribute("table", table)
        model.addAttribute("createTableStatement", tableService.getCreateTableStatement(table))
        model.addAttribute("indices", indexService.get(tableName))
        model.addAttribute("schemaName", dataSourceProperties.name)

        return "table"
    }
}
