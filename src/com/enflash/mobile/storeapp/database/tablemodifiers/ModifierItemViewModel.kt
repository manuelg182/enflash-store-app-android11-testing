package com.enflash.mobile.storeapp.database.tablemodifiers

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ModifierItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ModifierItemRepository(application)

    val modifiers = repository.getModifiers()

    fun getModifiers(productoId: String): List<ModifierItem> {
        return repository.getModifiers(productoId)
    }

    fun saveProducto(order: ModifierItem) {
        repository.insert(order)
    }

    fun deleteProducto(producto: ModifierItem) {
        repository.delete(producto)
    }

    fun getProductoById(id: String): List<ModifierItem> {
        return repository.getProductosByID(id)
    }

    fun deleteAll() {
        return repository.deleteAll()
    }
}