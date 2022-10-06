package com.enflash.mobile.storeapp.database.tableproductos

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.enflash.mobile.storeapp.database.OrdersDataActivity.Companion.getInstance

class OrderItemRepository(application: Application) {

    private val orderItemDao: OrderItemDao? = getInstance(application)?.orderItemDao()

    fun getItems(): LiveData<List<OrderItem>> {
        return orderItemDao?.getItems()!!
    }

    fun insert(order: OrderItem) {
        if (orderItemDao != null) InsertAsyncTask(orderItemDao).execute(order)
    }

    fun delete(order: OrderItem) {
        if (orderItemDao != null) DeleteAsyncTask(orderItemDao).execute(order)
    }

    fun getItemsByOrderId(producto: String): List<OrderItem> {
        return orderItemDao?.getItemsByOrderId(producto)!!
    }

    fun countProducts(orderId: String): Int {
        return orderItemDao?.countProducts(orderId)!!
    }

    fun getProductosByID(id: String): OrderItem {
        return orderItemDao?.findProductoById(id)!!
    }

    fun deleteAll() {
        return orderItemDao?.deleteAll()!!
    }

    private class InsertAsyncTask(private val orderItem: OrderItemDao) :
            AsyncTask<OrderItem, Void, Void>() {

        override fun doInBackground(vararg orders: OrderItem?): Void? {
            for (producto in orders) {
                if (producto != null) orderItem.insert(producto)
            }
            return null
        }
    }

    private class DeleteAsyncTask(private val orderItem: OrderItemDao) :
            AsyncTask<OrderItem, Void, Void>() {

        override fun doInBackground(vararg orders: OrderItem?): Void? {
            for (producto in orders) {
                if (producto != null) orderItem.delete(producto)
            }
            return null
        }
    }
}