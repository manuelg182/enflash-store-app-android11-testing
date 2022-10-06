package com.enflash.mobile.storeapp.ordenes.interfaces

import com.enflash.mobile.storeapp.database.tableorders.Order

interface ClickGeneric {
    fun resultClick(result: Order, position: Int)
}