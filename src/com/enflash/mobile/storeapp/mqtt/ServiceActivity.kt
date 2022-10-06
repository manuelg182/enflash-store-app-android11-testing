package com.enflash.mobile.storeapp.mqtt

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.application.AppHelper
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItem
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemRepository
import com.enflash.mobile.storeapp.database.tableorders.OrderRepository
import com.enflash.mobile.storeapp.database.tableproductos.OrderItem
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemRepository
import com.enflash.mobile.storeapp.events.NewOrderEvent
import com.enflash.mobile.storeapp.events.OrderNotAssignedEvent
import com.enflash.mobile.storeapp.events.StatusChangeEvent
import com.enflash.mobile.storeapp.events.StatusEvent
import com.enflash.mobile.storeapp.main.ApiClient
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus
import com.enflash.mobile.storeapp.main.model.ResponseStore
import com.enflash.mobile.storeapp.main.model.ResponseNewOrder
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.Constants
import com.enflash.mobile.storeapp.utils.FileLog
import com.enflash.mobile.storeapp.utils.PreferencesManager
import com.enflash.mobile.storeapp.utils.ShowNotification
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class ServiceActivity : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        ordersRepository = OrderRepository(App.getAppInstance())
        orderItemsRepository = OrderItemRepository(App.getAppInstance())
        modifiersRepository = ModifierItemRepository(App.getAppInstance())
        mqttManager = MqttManager(UUID.randomUUID().toString(), Constants.CUSTOMER_SPECIFIC_IOT_ENDPOINT)
        apiClient = ApiClient(applicationContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        connect()
        return START_STICKY
    }

    companion object {
        val LOG_TAG = ServiceActivity::class.java.canonicalName
        var mqttManager: MqttManager? = null
        private var apiClient: ApiClient? = null
        private var ordersRepository: OrderRepository? = null
        private var orderItemsRepository: OrderItemRepository? = null
        private var modifiersRepository: ModifierItemRepository? = null

        fun connect() {
            if(PreferencesManager.getCompanyId() > 0) {
                try {
                    if (mqttManager != null && mqttManager!!.status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                        runOnUiThread {

                            var username = AppHelper.getCurrUser()
                            if (username != null) {
                                AppHelper.getPool().getUser(username)?.getSessionInBackground(object : AuthenticationHandler {
                                    override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
                                        val credentialsProvider = CognitoCachingCredentialsProvider(
                                                App.getAppInstance().applicationContext,  // Context
                                                Constants.IDENTITY_POOL_ID,  // Identity Pool ID
                                                Regions.US_EAST_1 // Region
                                        )

                                        var logins: MutableMap<String, String> = HashMap()
                                        logins[Constants.COGNITO] = userSession.idToken.jwtToken
                                        credentialsProvider.logins = logins
                                        mqttManager = MqttManager(UUID.randomUUID().toString(), Constants.CUSTOMER_SPECIFIC_IOT_ENDPOINT)
                                        mqttManager!!.connect(credentialsProvider) { status, throwable ->
                                            runOnUiThread {
                                                Log.i(LOG_TAG, "Status = $status")
                                                FileLog.writeToConsole("$LOG_TAG - Status = $status")
                                                sendStatus(status)
                                            }
                                        }
                                    }

                                    override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation, userId: String?) {
                                        Log.i(LOG_TAG, authenticationContinuation.toString())
                                    }

                                    override fun getMFACode(continuation: MultiFactorAuthenticationContinuation) {}
                                    override fun authenticationChallenge(continuation: ChallengeContinuation) {}
                                    override fun onFailure(e: Exception) {
                                        sendStatus(mqttManager!!.status)
                                        Log.i(LOG_TAG, e.message!!)
                                        FileLog.writeToConsole("$LOG_TAG - ${e.message}")
                                    }
                                })
                            } else {
                                sendStatus(mqttManager!!.status)
                            }
                        }
                    } else {
                        if(mqttManager != null) {
                            sendStatus(mqttManager!!.status)
                        }
                    }
                } catch (e: Exception) {
                    if(mqttManager != null) {
                        sendStatus(mqttManager!!.status)
                    }
                    if(e.message != null){
                        FileLog.writeToConsole("$LOG_TAG - ${e.message}")
                    }
                }
            }
        }

        fun sendStatus(status: AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus) {
            when(status) {
                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                    subscribeNewOrders()
                }
            }
            EventBus.getDefault().post(StatusEvent(status))
        }

        private fun sendNewOrder(order: ResponseNewOrder) {
            EventBus.getDefault().post(NewOrderEvent(order))
        }

        private fun subscribeNewOrders() {
            if (mqttManager!!.status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                val topic = "store/${PreferencesManager.getCompanyId()}/orders/new"
                try {
                    mqttManager!!.subscribeToTopic(topic, AWSIotMqttQos.QOS1
                    ) { topic, data ->
                        run {
                            try {
                                val message = String(data, Charset.forName("UTF-8"))
                                val order = Gson().fromJson(message, ResponseNewOrder::class.java)
                                saveOrder(order)
                                ShowNotification.sendNotification("Nueva orden con folio: ${order.folio} con ${order.items!!.size} productos")
                                sendNewOrder(order)
                                Log.d(LOG_TAG, "   Topic: $topic")
                                Log.d(LOG_TAG, " Message: $message")
                            } catch (e: UnsupportedEncodingException) {
                                Log.e(LOG_TAG, "Message encoding error.", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Subscription error.", e)
                    FileLog.writeToConsole("$LOG_TAG - ${e.message}")
                }
            }
        }

        private fun saveOrder(order: ResponseNewOrder) {
            if (!ordersRepository!!.exist(order.orderId!!)) {
                var status = OrderStatus.arriving
                if (PreferencesManager.getConfigStoreAutoAccept()) {
                    status = OrderStatus.accepted
                }
                var orderItem = com.enflash.mobile.storeapp.database.tableorders.Order(
                        order.orderId!!, order.notes, order.folio, order.thumbnail, order.tip!!,
                        order.total!!, order.discount!!, order.totalPaid!!, order.totalToPay!!, order.paymentMethod,
                        Date().time, order.averageTime!!, status.name)
                ordersRepository!!.insert(orderItem)

                for (item in order.items!!) {
                    var productoItem = OrderItem(item.uuid, orderItem.orderId, item.productId.toString(),
                            item.productPrice!!, item.quantity!!.toInt(), item.description!!,
                            item.price!!, item.discount!!, item.total!!)

                    orderItemsRepository!!.insert(productoItem)

                    for (child in item.modifiers!!) {
                        for (childitem in child.selection) {
                            var modifier = ModifierItem(childitem.uuid!!, childitem.id!!,
                                    productoItem.uuid, childitem.name!!, child.name!!, childitem.quantity!!,
                                    childitem.price!!.toDouble())
                            modifiersRepository!!.insert(modifier)
                        }
                    }
                }
                if (status == OrderStatus.accepted) {
                    requestAcceptNewOrderRequest(order.orderId!!)
                } else {
                    requestOrderMarkAsArriving(order.orderId!!)
                }
            }else{
                if (PreferencesManager.getConfigStoreAutoAccept()) {
                    requestAcceptNewOrderRequest(order.orderId!!)
                }else{
                    requestOrderMarkAsArriving(order.orderId!!)
                }
            }
        }

        private fun requestAcceptNewOrderRequest(orderId: String) {
            val dto = ResponseOrderStatus()
            dto.companyId = PreferencesManager.getCompanyId()
            dto.status = OrderStatus.accepted
            dto.orderId = orderId
            val register: Call<ResponseOrderStatus> = apiClient!!.acceptNewOrderRequest(dto)
            register.enqueue(object : Callback<ResponseOrderStatus?> {
                override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                    Log.e(MainActivity.LOG_TAG, t!!.message!!)
                    FileLog.writeToConsole(t!!.message!!)
                    Toast.makeText(App.getAppInstance().applicationContext, "Ocurrio un error al aceptar la orden ${orderId}", Toast.LENGTH_LONG).show()
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

        private fun requestOrderMarkAsArriving(orderId: String) {
            val dto = ResponseStore()
            dto.companyId = PreferencesManager.getCompanyId()
            dto.datetime = Date().time / 1000
            dto.status = OrderStatus.ready
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
                        FileLog.writeToConsole("Ocurrio un error al actualizar (ready) la orden ${orderId}\n" +
                                "${response!!.code()} ${response!!.message()}")
                    }
                }
            })
        }
    }
}