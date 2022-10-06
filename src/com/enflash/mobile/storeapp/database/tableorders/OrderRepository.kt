package com.enflash.mobile.storeapp.database.tableorders

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.enflash.mobile.storeapp.database.OrdersDataActivity.Companion.getInstance
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemDao
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemDao
import com.enflash.mobile.storeapp.main.model.DateTimeDto
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import java.sql.Date
import java.sql.Timestamp

class OrderRepository(application: Application) {

    private val orderDao: OrderDao? = getInstance(application)?.orderDao()

    fun save(order: Order) {
        orderDao?.insert(order)
    }

    fun insert(order: Order) {
        if (orderDao != null) InsertAsyncTask(orderDao).execute(order)
    }

    fun update(order: Order) {
        orderDao?.update(order)
    }

    fun updateOrderStatus(orderId: String, status: String) {
        orderDao?.updateOrderStatus(orderId, status)
    }

    fun delete(order: Order) {
        if (orderDao != null) DeleteAsyncTask(orderDao).execute(order)
    }

    fun getOrders(): List<Order> {
        return orderDao?.getOrders()!!
    }

    fun existOrderByAccept(): Boolean{
        return orderDao?.existOrderByAccept(OrderStatus.arriving.name, 120L)!!
    }

    fun getOrderByRejectWithoutAccepted(): List<Order> {
        return orderDao?.getOrderByRejectWithoutAccepted(OrderStatus.arriving.name, 660L)!!
    }

    fun existOrderByMarkAsReady(): Boolean{
        return orderDao?.existOrderByMarkAsReady(OrderStatus.accepted.name, 0L)!!
    }

    fun changeOrderStatusByStatus(statusOld: String, statusNew: String) {
        return orderDao?.changeOrderStatusByStatus(statusOld, statusNew)!!
    }

    fun exist(id: String): Boolean {
        return orderDao?.exist(id)!!
    }

    fun existByOrderIdAndStatus(id: String, status: String): Boolean {
        return orderDao?.existByOrderIdAndStatus(id, status)!!
    }

    fun getOrdersArriving(): LiveData<List<Order>> {
        return orderDao?.getOrdersByStatus(OrderStatus.arriving.name)!!
    }

    fun getOrdersAccepted(): LiveData<List<Order>> {
        return orderDao?.getOrdersByStatus(OrderStatus.accepted.name)!!
    }

    fun getOrdersReady(): LiveData<List<Order>> {
        return orderDao?.getOrdersByStatus(OrderStatus.ready.name)!!
    }

    fun getOrdersCollected(): LiveData<List<Order>> {
        return orderDao?.getOrdersByStatus(OrderStatus.collected.name)!!
    }

    fun getOrdersDelivered(): LiveData<List<Order>> {
        return orderDao?.getOrdersByStatus(OrderStatus.delivered.name)!!
    }

    fun deleteAll() {
        return orderDao?.deleteAll()!!
    }

    fun getOrderById(id: String): Order {
        return orderDao?.findOrderById(id)!!
    }

    fun deleteOrderPrevious(now: Long) {
        orderDao?.deleteOrderPrevious(now)
    }

    fun getOrderPrevious(now: Long): List<String>{
        return orderDao?.getOrderPrevious(now)!!
    }

    private class InsertAsyncTask(private val orderDao: OrderDao) :
            AsyncTask<Order, Void, Void>() {

        override fun doInBackground(vararg orders: Order?): Void? {
            for (order in orders) {
                if (order != null) orderDao.insert(order)
            }
            return null
        }
    }

    private class DeleteAsyncTask(private val orderDao: OrderDao) :
            AsyncTask<Order, Void, Void>() {

        override fun doInBackground(vararg orders: Order?): Void? {
            for (order in orders) {
                if (order != null) orderDao.delete(order)
            }
            return null
        }
    }
}