package com.enflash.mobile.storeapp.main

import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItem
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemRepository
import com.enflash.mobile.storeapp.database.tableorders.OrderRepository
import com.enflash.mobile.storeapp.database.tableproductos.OrderItem
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemRepository
import com.enflash.mobile.storeapp.main.model.RequestPushTokenPost
import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus
import com.enflash.mobile.storeapp.main.model.ResponseStore
import com.enflash.mobile.storeapp.main.model.ResponseNewOrder
import com.enflash.mobile.storeapp.ordenes.data.OrderInfoStatus
import com.enflash.mobile.storeapp.ordenes.data.OrderNewRequest
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.FileLog
import com.enflash.mobile.storeapp.utils.PreferencesManager
import com.enflash.mobile.storeapp.utils.ShowNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class FirebaseHandlerService : FirebaseMessagingService() {

    private var ordersRepository: OrderRepository? = null
    private var orderItemsRepository: OrderItemRepository? = null
    private var modifiersRepository: ModifierItemRepository? = null
    private lateinit var apiClient: ApiClient

    override fun onCreate() {
        ordersRepository = OrderRepository(App.getAppInstance())
        orderItemsRepository = OrderItemRepository(App.getAppInstance())
        modifiersRepository = ModifierItemRepository(App.getAppInstance())
        apiClient = ApiClient(App.getAppInstance().applicationContext)
        queue = ConcurrentHashMap<String, ResponseNewOrder>()
    }

    override fun onNewToken(p0: String) {
        Log.i(LOG_TAG, p0)
        val dto = RequestPushTokenPost()
        dto.id = "store-${PreferencesManager.getCompanyId()}"
        dto.platform = "gcm"
        dto.type = "store"
        dto.token = p0
        dto.data = "store-${PreferencesManager.getCompanyId()}"
        val register: Call<Boolean> = apiClient.registerPushToken(dto)
        register.enqueue(object : Callback<Boolean?> {
            override fun onFailure(@Nullable call: Call<Boolean?>?, @Nullable t: Throwable?) {
                Log.e(LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
            }

            override fun onResponse(call: Call<Boolean?>, response: retrofit2.Response<Boolean?>) {
                if (response.isSuccessful) {
                    Log.e(LOG_TAG, "Token actualizado")
                    FileLog.writeToConsole("Token actualizado")
                }
            }
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(LOG_TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            run {
                if (remoteMessage.data.containsKey("messageType")) {
                    val messageType = remoteMessage.data["messageType"]!!
                    when (messageType) {
                        "new-order" -> {
                            try {
                                if (PreferencesManager.getConfigStoreOperationStatus()) {
                                    Log.d(LOG_TAG, "Message data extraData: ${remoteMessage.data["extraData"]}")
                                    FileLog.writeToConsole("Message data extraData: ${remoteMessage.data["extraData"]}")
                                    val order = Gson().fromJson(remoteMessage.data["extraData"], OrderNewRequest::class.java)
                                    requestGetNewOrderRequest(order.companyId, order.orderId)
                                }
                            } catch (ex: Exception) {
                                FileLog.writeToConsole(ex.message!!)
                            }
                        }
                        "order-collected" -> {
                            try {
                                Log.d(LOG_TAG, "Message data extraData: ${remoteMessage.data["extraData"]}")
                                FileLog.writeToConsole("Message data extraData: ${remoteMessage.data["extraData"]}")
                                val order = Gson().fromJson(remoteMessage.data["extraData"], OrderInfoStatus::class.java)
                                ordersRepository?.updateOrderStatus(order.orderId!!, order.status!!.name)
                                MainActivity.notifyAdapterChanged()
                            } catch (ex: Exception) {
                                FileLog.writeToConsole(ex.message!!)
                            }
                        }
                        "order-delivered" -> {
                            try {
                                Log.d(LOG_TAG, "Message data extraData: ${remoteMessage.data["extraData"]}")
                                FileLog.writeToConsole("Message data extraData: ${remoteMessage.data["extraData"]}")
                                val order = Gson().fromJson(remoteMessage.data["extraData"], OrderInfoStatus::class.java)
                                ordersRepository?.updateOrderStatus(order.orderId!!, order.status!!.name)
                                MainActivity.notifyAdapterChanged()
                            } catch (ex: Exception) {
                                FileLog.writeToConsole(ex.message!!)
                            }
                        }
                        "order-not-assigned" -> {
                            try {
                                Log.d(LOG_TAG, "Message data extraData: ${remoteMessage.data["extraData"]}")
                                FileLog.writeToConsole("Message data extraData: ${remoteMessage.data["extraData"]}")
                                val order = Gson().fromJson(remoteMessage.data["extraData"], ResponseNewOrder::class.java)
                                ordersRepository!!.updateOrderStatus(order.orderId!!, OrderStatus.accepted.name)
                                ShowNotification.sendNotification("La orden con folio: ${order.folio} no fue asignada")
                            } catch (ex: Exception) {
                                FileLog.writeToConsole(ex.message!!)
                            }
                        }
                    }
                } else {
                    ShowNotification.sendNotification("${remoteMessage.data["message"]}")
                }
            }
        }
        remoteMessage.notification?.let {
            Log.d(LOG_TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun saveOrder(order: ResponseNewOrder) {
        if (!queue.containsKey(order.orderId)) {
            queue[order.orderId!!] = order
            ShowNotification.sendNotification(order.createdAt!!.toInt(),
                    "Nueva orden con folio: ${order.folio} con ${order.items!!.size} productos")
            var status = OrderStatus.arriving
            if (PreferencesManager.getConfigStoreAutoAccept()) {
                status = OrderStatus.accepted
            }
            var orderItem = com.enflash.mobile.storeapp.database.tableorders.Order(
                    order.orderId!!, order.notes, order.folio, order.thumbnail, order.tip!!,
                    order.total!!, order.discount!!,  order.totalPaid!!, order.totalToPay!!, order.paymentMethod,
                    order.createdAt!!, order.averageTime!!, status.name)
            ordersRepository?.save(orderItem)

            for (item in order.items!!) {
                var productoItem = OrderItem(item.uuid, orderItem.orderId, item.productId.toString(),
                        item.productPrice!!, item.quantity!!.toInt(), item.description!!,
                        item.price!!, item.discount!!, item.total!!)

                orderItemsRepository?.insert(productoItem)

                for (child in item.modifiers!!) {
                    for (childitem in child.selection) {
                        var modifier = ModifierItem(childitem.uuid, childitem.id!!,
                                productoItem.uuid, childitem.name!!, child.name!!,
                                childitem.quantity!!, childitem.price!!.toDouble())
                        modifiersRepository?.insert(modifier)
                    }
                }
            }
            if (status == OrderStatus.accepted) {
                requestAcceptNewOrderRequest(order.orderId!!, order)
            } else {
                requestOrderMarkAsArriving(order.orderId!!, order)
            }
        } else {
            if (PreferencesManager.getConfigStoreAutoAccept()) {
                requestAcceptNewOrderRequest(order.orderId!!, order)
            } else {
                requestOrderMarkAsArriving(order.orderId!!, order)
            }
        }
    }

    private fun requestAcceptNewOrderRequest(orderId: String, order: ResponseNewOrder) {
        val dto = ResponseOrderStatus()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.status = OrderStatus.accepted
        dto.orderId = orderId
        val register: Call<ResponseOrderStatus> = apiClient!!.acceptNewOrderRequest(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                Toast.makeText(applicationContext, "Ocurrio un error al aceptar la orden ${orderId}", Toast.LENGTH_LONG).show()
                FileLog.writeToConsole(t!!.message!!)
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (response.isSuccessful) {
                    ordersRepository!!.updateOrderStatus(orderId, OrderStatus.accepted.name)
                    MainActivity.notifyAdapterChanged()
                } else {
                    FileLog.writeToConsole("Ocurrio un error al aceptar la orden ${orderId}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
            }
        })
    }

    private fun requestOrderMarkAsArriving(orderId: String, order: ResponseNewOrder) {
        val dto = ResponseStore()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.datetime = Date().time / 1000
        dto.status = OrderStatus.arriving
        dto.orderId = orderId
        val register: Call<ResponseOrderStatus> = apiClient!!.orderMarkAsArriving(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (response.isSuccessful) {
                    ordersRepository!!.updateOrderStatus(orderId, response.body()!!.status!!.name)
                    MainActivity.notifyAdapterChanged()
                } else {
                    FileLog.writeToConsole("Ocurrio un error en la llegada de la orden ${orderId}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
            }
        })
    }

    private fun requestGetNewOrderRequest(companyId: Long, orderId: String) {
        val register: Call<ResponseNewOrder> = apiClient.orderGetNewRequest(companyId, orderId)
        register.enqueue(object : Callback<ResponseNewOrder?> {
            override fun onFailure(@Nullable call: Call<ResponseNewOrder?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
            }

            override fun onResponse(call: Call<ResponseNewOrder?>, response: retrofit2.Response<ResponseNewOrder?>) {
                if (response.isSuccessful) {
                    saveOrder(response.body()!!)
                } else {
                    Log.e(MainActivity.LOG_TAG, "Ocurrio un error al recibir la orden ${orderId}\n" +
                            "${response!!.code()} ${response!!.message()}")
                    FileLog.writeToConsole("Ocurrio un error al recibir la orden ${orderId}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
            }
        })
    }


    companion object {
        val LOG_TAG = FirebaseHandlerService::class.java.canonicalName
        lateinit var queue: ConcurrentHashMap<String, ResponseNewOrder>
    }
}