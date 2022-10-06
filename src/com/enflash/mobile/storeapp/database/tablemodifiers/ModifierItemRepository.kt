package com.enflash.mobile.storeapp.database.tablemodifiers

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.enflash.mobile.storeapp.database.OrdersDataActivity.Companion.getInstance

class ModifierItemRepository(application: Application) {

    private val modifierItemDao: ModifierItemDao? = getInstance(application)?.modifierItemDao()

    fun insert(producto: ModifierItem) {
        if (modifierItemDao != null) InsertAsyncTask(modifierItemDao).execute(producto)
    }

    fun delete(producto: ModifierItem) {
        if (modifierItemDao != null) DeleteAsyncTask(modifierItemDao).execute(producto)
    }

    fun getModifiers(): LiveData<List<ModifierItem>> {
        return modifierItemDao?.getModifier()!!
    }

    fun deleteAll() {
        return modifierItemDao?.deleteAll()!!
    }

    fun getModifiers(productoId: String): List<ModifierItem> {
        return modifierItemDao?.getModifiers(productoId)!!
    }

    fun getProductosByID(id: String): List<ModifierItem> {
        return modifierItemDao?.findModifierById(id)!!
    }

    private class InsertAsyncTask(private val productoItem: ModifierItemDao) :
            AsyncTask<ModifierItem, Void, Void>() {

        override fun doInBackground(vararg productos: ModifierItem?): Void? {
            for (producto in productos) {
                if (producto != null) productoItem.insert(producto)
            }
            return null
        }
    }

    private class DeleteAsyncTask(private val productoItem: ModifierItemDao) :
            AsyncTask<ModifierItem, Void, Void>() {

        override fun doInBackground(vararg productos: ModifierItem?): Void? {
            for (producto in productos) {
                if (producto != null) productoItem.delete(producto)
            }
            return null
        }
    }
}