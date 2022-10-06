package com.enflash.mobile.storeapp.database.tableorders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemRepository
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemRepository
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.utils.FileLog
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository(application)
    private val repositoryItems = OrderItemRepository(application)
    private val repositoryModifiers = ModifierItemRepository(application)

    val ordersAccepted = repository.getOrdersAccepted()
    val ordersArriving = repository.getOrdersArriving()
    val ordersReady = repository.getOrdersReady()
    val ordersCollected = repository.getOrdersCollected()
    val ordersDelivered = repository.getOrdersDelivered()

    fun saveOrder(order: Order) {
        repository.insert(order)
    }

    fun exist(id: String): Boolean {
        return repository.exist(id)
    }

    fun getOrders(): List<Order>{
        return repository.getOrders()
    }

    fun existByOrderIdAndStatus(id: String, status: String): Boolean {
        return repository.existByOrderIdAndStatus(id, status)
    }

    fun updateOrder(order: Order) {
        repository.update(order)
    }

    fun changeOrderStatusByStatus(statusOld: String, statusNew: String) {
        repository.changeOrderStatusByStatus(statusOld, statusNew)
    }

    fun updateOrderStatus(orderId: String, status: String) {
        repository.updateOrderStatus(orderId, status)
    }

    fun delete(order: Order) {
        repository.delete(order)
    }

    fun getOrderById(id: String): Order {
        return repository.getOrderById(id)
    }

    fun deleteAll() {
        return repository.deleteAll()
    }

    fun existOrderByAccept(): Boolean {
        return repository.existOrderByAccept()
    }

    fun getOrderByRejectWithoutAccepted(): List<Order> {
        return repository.getOrderByRejectWithoutAccepted()
    }

    fun existOrderByMarkAsReady(): Boolean {
        return repository.existOrderByMarkAsReady()
    }

    fun deleteOrderPrevious() {
        try {
            var dateNow = Date().time
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var format = simpleDateFormat.format(dateNow)
            var timestamp = Timestamp.valueOf("$format 00:00:00")
            val calendar = Calendar.getInstance()
            calendar.time = Date(timestamp.time)
            calendar.add(Calendar.HOUR_OF_DAY, 29)
            val now = calendar.time.time / 1000
            val orders = repository.getOrderPrevious(now)
            if (orders.isNotEmpty()) {
                Log.i(MainActivity.LOG_TAG, "Ordenes a borrar: ${orders}")
                FileLog.writeToConsole("Ordenes a borrar: ${orders}")
                for (order in orders) {
                    val orderItems = repositoryItems.getItemsByOrderId(order)
                    for (orderItem in orderItems!!) {
                        val modifiers = repositoryModifiers.getModifiers(orderItem.uuid)
                        for (mod in modifiers) {
                            repositoryModifiers.delete(mod)
                        }
                        repositoryItems.delete(orderItem)
                    }
                }
            }
            repository.deleteOrderPrevious(now)
        } catch (ex: Exception) {
            Log.i(MainActivity.LOG_TAG, "Error en borrado de ordenes: ${ex.message}")
            FileLog.writeToConsole("Error en borrado de ordenes: ${ex.message}")
        }
    }

}