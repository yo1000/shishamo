package com.yo1000.shishamo.controller.api.v1

import com.yo1000.shishamo.model.Table
import com.yo1000.shishamo.service.TableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author su-kun1899
 * @author yo1000
 */
@RestController
@RequestMapping("v1/tables")
class TablesRestController(
        val tableService: TableService
) {
    @GetMapping
    fun get(): List<Table> = tableService.get()

    @GetMapping(path = arrayOf("{tableName}"))
    fun get(@PathVariable tableName: String): Table = tableService.get(tableName)
}
