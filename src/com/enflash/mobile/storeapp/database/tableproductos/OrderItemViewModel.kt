package com.enflash.mobile.storeapp.database.tableproductos

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class OrderItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderItemRepository(application)

    var productos = repository.getItems()

    fun getItemsByOrderId(producto: String): List<OrderItem> {
        return repository.getItemsByOrderId(producto)
    }

    fun saveProducto(order: OrderItem) {
        repository.insert(order)
    }

    fun deleteProducto(order: OrderItem) {
        repository.delete(order)
    }

    fun countProducts(orderId: String): Int {
        return repository.countProducts(orderId)
    }

    fun getProductoById(id: String): OrderItem {
        return repository.getProductosByID(id)
    }

    fun deleteAll() {
        return repository.deleteAll()
    }
}