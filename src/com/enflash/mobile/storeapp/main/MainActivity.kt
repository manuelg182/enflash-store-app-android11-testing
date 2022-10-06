package com.enflash.mobile.storeapp.main

import android.Manifest
import android.content.*
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.application.AppHelper
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemViewModel
import com.enflash.mobile.storeapp.database.tableorders.OrderViewModel
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemViewModel
import com.enflash.mobile.storeapp.events.NewOrderEvent
import com.enflash.mobile.storeapp.events.OrderNotAssignedEvent
import com.enflash.mobile.storeapp.events.StatusEvent
import com.enflash.mobile.storeapp.main.adapter.ListAdapter
import com.enflash.mobile.storeapp.main.adapter.ListReadyAdapter
import com.enflash.mobile.storeapp.main.config.ConfiguracionActivity
import com.enflash.mobile.storeapp.main.config.models.*
import com.enflash.mobile.storeapp.main.enums.StoreStatus
import com.enflash.mobile.storeapp.main.model.*
import com.enflash.mobile.storeapp.mqtt.ServiceActivity
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.*
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    var apiClient: ApiClient? = null
    var batteryLevel: String? = null
    var username: String? = null
    var cognitoUser: CognitoUser? = null
    var ordersViewModel: OrderViewModel? = null
    var orderItemsViewModel: OrderItemViewModel? = null
    var modifiersViewModel: ModifierItemViewModel? = null
    var permisionok = false
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val thread: Thread = object : Thread() {
        override fun run() {
            try {
                while (!this.isInterrupted) {
                    sleep(30000)
                    runOnUiThread {
                        alertOrderByAccept()
                        notifyAdapterChanged()
                    }
                    sleep(30000)
                    runOnUiThread {
                        alertOrderByMarkAsReady()
                        notifyAdapterChanged()
                    }
                }
            } catch (e: InterruptedException) {
                Log.e(LOG_TAG!!, e.message!!)
                FileLog.writeToConsole("${LOG_TAG} - ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
            username = AppHelper.getCurrUser()
            cognitoUser = AppHelper.getPool().getUser(username)
            if (!PreferencesManager.getLogoBanner().isNullOrEmpty()) {
                val options: RequestOptions = RequestOptions()
                        .placeholder(R.drawable.banner)
                        .error(R.drawable.banner)
                Glide.with(App.getAppInstance().applicationContext).load(
                        Constants.URL_BASE_ENFLASH + PreferencesManager.getLogoBanner()
                ).apply(options).into(store_banner)
            }
            val tabletSize = resources.getBoolean(R.bool.isTablet)

            config_sections_and_products.setOnClickListener {
                startActivity(Intent(this, ConfiguracionActivity::class.java))
            }

            apiClient = ApiClient(this)

            initialize()

            modifiersViewModel = ViewModelProvider(this).get(ModifierItemViewModel::class.java)
            ordersViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
            orderItemsViewModel = ViewModelProvider(this).get(OrderItemViewModel::class.java)

            val yourListViewArriving = findViewById<View>(R.id.itemListView) as RecyclerView
            if (tabletSize) {
                val rotation: Int = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getOrientation()
                var column = 3
                if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
                    column = 2
                }
                yourListViewArriving.layoutManager = GridLayoutManager(this, column)
                yourListViewArriving.addItemDecoration(GridSpacingItemDecoration(column, dpToPx(2), true))
            } else {
                yourListViewArriving.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            }
            yourListViewArriving.setHasFixedSize(true)
            yourListViewArriving.itemAnimator = DefaultItemAnimator()

            customAdapterArriving = ListAdapter(ordersArriving, this, ordersViewModel!!, orderItemsViewModel!!, apiClient!!)
            yourListViewArriving.adapter = customAdapterArriving

            ordersViewModel!!.ordersArriving.observe(this, androidx.lifecycle.Observer<List<com.enflash.mobile.storeapp.database.tableorders.Order>> { orderitems ->
                try {
                    if (orderitems != null && orderitems.isNotEmpty()) {
                        ordersArriving.clear()
                        ordersArriving.addAll(orderitems.asReversed())
                        customAdapterArriving!!.notifyDataSetChanged()
                        Log.d("Ordenes", "Ordenes: $orderitems")
                    } else {
                        ordersArriving.clear()
                        customAdapterArriving!!.notifyDataSetChanged()
                    }
                    for (value in countDownTimerArriving.values) {
                        value.cancel()
                    }
                    countDownTimerArriving.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            val yourListViewAccepted = findViewById<View>(R.id.itemListView2) as RecyclerView

            if (tabletSize) {
                val rotation: Int = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getOrientation()
                var column = 3
                if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
                    column = 2
                }
                yourListViewAccepted.layoutManager = GridLayoutManager(this, column)
                yourListViewAccepted.addItemDecoration(GridSpacingItemDecoration(column, dpToPx(2), true))
            } else {
                yourListViewAccepted.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            }
            yourListViewAccepted.setHasFixedSize(true)
            yourListViewAccepted.itemAnimator = DefaultItemAnimator()

            customAdapterAccepted = ListAdapter(ordersAccepted, this, ordersViewModel!!, orderItemsViewModel!!, apiClient!!)
            yourListViewAccepted.adapter = customAdapterAccepted

            ordersViewModel!!.ordersAccepted.observe(this, androidx.lifecycle.Observer<List<com.enflash.mobile.storeapp.database.tableorders.Order>> { orderitems ->
                try {
                    if (orderitems != null && orderitems.isNotEmpty()) {
                        ordersAccepted.clear()
                        ordersAccepted.addAll(orderitems.asReversed())
                        customAdapterAccepted!!.notifyDataSetChanged()
                        Log.d("Ordenes", "Ordenes: ${orderitems}")
                    } else {
                        ordersAccepted.clear()
                        customAdapterAccepted!!.notifyDataSetChanged()
                    }
                    for (value in countDownTimerAccepted.values) {
                        value.cancel()
                    }
                    countDownTimerAccepted.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            val yourListViewReady = findViewById<View>(R.id.itemListView3) as RecyclerView

            if (tabletSize) {
                val rotation: Int = (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getOrientation()
                var column = 3
                if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
                    column = 2
                }
                yourListViewReady.layoutManager = GridLayoutManager(this, column)
                yourListViewReady.addItemDecoration(GridSpacingItemDecoration(column, dpToPx(2), true))
            } else {
                yourListViewReady.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            }
            yourListViewReady.setHasFixedSize(true)
            yourListViewReady.itemAnimator = DefaultItemAnimator()

            customAdapterReady = ListReadyAdapter(ordersReady, this, orderItemsViewModel!!)
            yourListViewReady.adapter = customAdapterReady

            ordersViewModel!!.ordersReady.observe(this, androidx.lifecycle.Observer<List<com.enflash.mobile.storeapp.database.tableorders.Order>> { orderitems ->
                try {
                    if (orderitems != null && orderitems.isNotEmpty()) {
                        ordersReady.clear()
                        ordersReady.addAll(orderitems.asReversed())
                        customAdapterReady!!.notifyDataSetChanged()
                        Log.d("Ordenes", "Ordenes: ${orderitems}")
                    } else {
                        ordersReady.clear()
                        customAdapterReady!!.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener {
                updateStatusOrder()
                swipeRefreshLayout!!.isRefreshing = false
            }

            store_banner.setOnLongClickListener {
                sendEmail("zballina@gmail.com", "Enflash Log", "Compartir bitacora de errores")
            }
            history_orders.setOnClickListener {
                startActivity(Intent(this, OrdersActivity::class.java))
            }
            FirebaseMessaging.getInstance().isAutoInitEnabled = true

            if (!AppCenter.isConfigured()) {
                AppCenter.start(application, "3cdc434c-91ec-467c-be7f-719df7f0aef2",
                        Analytics::class.java, Crashes::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateStatusOrder(){
        ordersViewModel!!.getOrders().forEach { o ->
            val register: Call<ResponseNewOrder> = apiClient!!.orderGetNewRequest(
                    PreferencesManager.getCompanyId(), o.orderId)
            register.enqueue(object : Callback<ResponseNewOrder?> {
                override fun onFailure(@Nullable call: Call<ResponseNewOrder?>?, @Nullable t: Throwable?) {
                    Log.e(LOG_TAG, t!!.message!!)
                    FileLog.writeToConsole(t!!.message!!)
                }

                override fun onResponse(call: Call<ResponseNewOrder?>, response: retrofit2.Response<ResponseNewOrder?>) {
                    if (response.isSuccessful) {
                        ordersViewModel!!.updateOrderStatus(o.orderId, response.body()!!.status!!.name)
                        notifyAdapterChanged()
                    } else {
                        Log.e(LOG_TAG, "Ocurrio un error al recibir la orden ${o.orderId}\n" +
                                "${response!!.code()} ${response!!.message()}")
                        FileLog.writeToConsole("Ocurrio un error al recibir la orden ${o.orderId}\n" +
                                "${response!!.code()} ${response!!.message()}")
                    }
                }
            })
        }
    }

    private fun alertOrderByAccept() {
        if (ordersViewModel!!.existOrderByAccept()) {
            Toast.makeText(applicationContext, "Alerta: existen ordenes que no se han aceptado", Toast.LENGTH_LONG).show()
            ShowNotification.sendAlertByAccept("Alerta: existen ordenes que no se han aceptado")
        }
    }

    private fun alertOrderByMarkAsReady() {
        if (ordersViewModel!!.existOrderByMarkAsReady()) {
            Toast.makeText(applicationContext, "Alerta: existen ordenes pendientes por preparar y marcar como listas", Toast.LENGTH_LONG).show()
            ShowNotification.sendAlertByMarkAsReady("Alerta: existen ordenes pendientes por preparar y marcar como listas")
        }
    }

    private fun validatePermission() {
        try {
            if (permisionok) {
                status_auto_accept!!.text = if (PreferencesManager.getConfigStoreAutoAccept()) "Auto aceptar" else "Aceptar manual"
                status_saturated!!.text = if (PreferencesManager.getConfigStoreSaturated()) "Saturado" else "Descargado"
                status_connection!!.text = if (PreferencesManager.getConfigStoreOperationStatus()) "En operación" else "Desconectado"
                config_operation_store!!.isChecked = PreferencesManager.getConfigStoreOperationStatus()
                config_auto_accept!!.isChecked = PreferencesManager.getConfigStoreAutoAccept()
                config_saturated!!.isChecked = PreferencesManager.getConfigStoreSaturated()
                if (PreferencesManager.getConfigStoreAutoAccept()) {
                    layout_list_arriving!!.visibility = View.GONE
                } else {
                    layout_list_arriving!!.visibility = View.VISIBLE
                }

                val product: Call<ResponseOperationalActions> = apiClient!!.getOperationalActions(PreferencesManager.getCompanyId())

                product.enqueue(object : Callback<ResponseOperationalActions?> {

                    override fun onFailure(@Nullable call: Call<ResponseOperationalActions?>?, @Nullable t: Throwable?) {
                        t?.printStackTrace()
                        status_auto_accept!!.text = "Aceptar manual"
                        status_saturated!!.text = "Descargado"
                        status_connection!!.text = "Desconectado"
                        config_operation_store!!.isChecked = false
                        config_auto_accept!!.isChecked = false
                        config_saturated!!.isChecked = false

                        config_operation_store!!.isEnabled = false
                        config_auto_accept!!.isEnabled = false
                        config_saturated!!.isEnabled = false
                        PreferencesManager.setConfigStoreOperationStatus(config_operation_store!!.isChecked)
                        PreferencesManager.setConfigStoreAutoAccept(config_auto_accept!!.isChecked)
                    }

                    override fun onResponse(call: Call<ResponseOperationalActions?>, response: retrofit2.Response<ResponseOperationalActions?>) {
                        if (response.isSuccessful) {
                            Log.d(LOG_TAG, "Message: " + response.body())
                            if (response.body() != null) {
                                val options: RequestOptions = RequestOptions()
                                        .placeholder(R.drawable.banner)
                                        .error(R.drawable.banner)
                                text_average_time_saturated!!.text = "${response.body()!!.averageTime} min"
                                PreferencesManager.setLogoBanner(response.body()!!.banner!!)
                                Glide.with(App.getAppInstance().applicationContext).load(
                                        Constants.URL_BASE_ENFLASH + PreferencesManager.getLogoBanner()
                                ).apply(options).into(store_banner)

                                config_auto_accept!!.isChecked = false
                                config_operation_store!!.isChecked = response.body()!!.enabled!!
                                config_saturated!!.isChecked = response.body()!!.saturated!!

                                PreferencesManager.setConfigStoreOperationStatus(config_operation_store!!.isChecked)
                                PreferencesManager.setConfigStoreAutoAccept(config_auto_accept!!.isChecked)
                                PreferencesManager.setConfigStoreSaturated(config_saturated!!.isChecked)
                                status_connection!!.text = if (config_operation_store!!.isChecked) "En operación" else "Desconectado"

                                if (config_auto_accept!!.isChecked) {
                                    layout_list_arriving!!.visibility = View.GONE
                                } else {
                                    layout_list_arriving!!.visibility = View.VISIBLE
                                }
                                config_auto_accept!!.isEnabled = false
                                config_operation_store!!.isEnabled = true
                                config_saturated!!.isEnabled = true
                                status_auto_accept!!.text = if (config_auto_accept!!.isChecked) "Auto aceptar" else "Aceptar manual"
                                status_saturated!!.text = if (config_saturated!!.isChecked) "Saturado" else "Descargado"
                                config_sections_and_products!!.isEnabled = true
                                PreferencesManager.setTimeToReject(response.body()!!.timeToReject)
                                ordersViewModel!!.deleteOrderPrevious()
                            } else {
                                Toast.makeText(applicationContext, "Este usuario no tiene una compañia válida configurada", Toast.LENGTH_LONG).show()
                                status_auto_accept!!.text = "Aceptar manual"
                                status_saturated!!.text = "Saturar"
                                status_connection!!.text = "Desconectado"
                                config_auto_accept!!.isEnabled = false
                                config_auto_accept!!.isChecked = false
                                config_operation_store!!.isEnabled = false
                                config_operation_store!!.isChecked = false
                                config_saturated!!.isEnabled = false
                                config_saturated!!.isChecked = false
                                config_sections_and_products!!.isEnabled = false
                                PreferencesManager.setConfigStoreAutoAccept(config_auto_accept!!.isChecked)
                                PreferencesManager.setConfigStoreOperationStatus(config_operation_store!!.isEnabled)
                            }
                        }
                    }
                })

            } else {
                status_auto_accept!!.text = "Aceptar manual"
                status_saturated!!.text = "Descargado"
                status_connection!!.text = "Desconectado"
                config_operation_store!!.isChecked = false
                config_auto_accept!!.isChecked = false
                config_saturated!!.isChecked = false
                config_operation_store!!.isEnabled = false
                config_auto_accept!!.isEnabled = false
                config_saturated!!.isEnabled = false
                PreferencesManager.setConfigStoreAutoAccept(config_auto_accept!!.isChecked)
                PreferencesManager.setConfigStoreOperationStatus(config_operation_store!!.isChecked)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initialize() {
        configOperationStore()
        configAutoAccepted()
        configSaturated()
        notifyAdapterChanged()
    }

    private fun configOperationStore() {
        config_operation_store.setOnClickListener() {
            if (PreferencesManager.getConfigStoreOperationStatus() != config_operation_store.isChecked) {
                try {
                    if (config_operation_store.isChecked) {
                        requestConfigOperationStore(config_operation_store.isChecked)
                        if (ServiceActivity!!.mqttManager!!.status != AWSIotMqttClientStatus.Connected) {
                            ServiceActivity!!.connect()
                        }
                    } else {
                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    requestConfigOperationStore(config_operation_store.isChecked)
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    config_operation_store.isChecked = true
                                }
                            }
                        }

                        val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.myDialog))
                        builder.setTitle("Atención")
                                .setCancelable(false)
                                .setMessage("¿Estas seguro de poner el modo desconexión?")
                                .setPositiveButton("Aceptar", dialogClickListener)
                                .setNegativeButton("Cancelar", dialogClickListener).show()

                    }
                } catch (ex: Exception) {
                    config_operation_store.isChecked = !config_operation_store.isChecked
                    Log.e(LOG_TAG, ex.message!!)
                    FileLog.writeToConsole("$LOG_TAG - ${ex.message}")
                }
            }
        }
    }

    private fun requestConfigOperationStore(isChecked: Boolean) {
        status_connection!!.text = if (PreferencesManager.getConfigStoreOperationStatus()) "Desconectando..." else "Conectando"
        val dto = StoreStatusChange()
        dto.storeId = PreferencesManager.getCompanyId()
        dto.status = if (isChecked) StoreStatus.open else StoreStatus.close
        val register: Call<ResponseStoreStatusChange> = apiClient!!.storeChangeConfig(dto)
        register.enqueue(object : Callback<ResponseStoreStatusChange?> {
            override fun onFailure(@Nullable call: Call<ResponseStoreStatusChange?>?, @Nullable t: Throwable?) {
                Log.e(LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
                config_operation_store.isChecked = !isChecked
                status_connection!!.text = if (PreferencesManager.getConfigStoreOperationStatus()) "En operación" else "Desconectado"
            }

            override fun onResponse(call: Call<ResponseStoreStatusChange?>, response: retrofit2.Response<ResponseStoreStatusChange?>) {
                if (response.isSuccessful) {
                    PreferencesManager.setConfigStoreOperationStatus(response.body()!!.status)
                    var leyend = if (response.body()!!.status!!) "activo" else "inactivo"
                    Toast.makeText(applicationContext, "El negocio ahora está ${leyend}", Toast.LENGTH_LONG).show()
                } else {
                    config_operation_store.isChecked = !isChecked
                    if (response != null) {
                        FileLog.writeToConsole("${response!!.body()!!}")
                        Log.e(LOG_TAG, "${response!!.body()!!}")
                    }
                }
                status_connection!!.text = if (PreferencesManager.getConfigStoreOperationStatus()) "En operación" else "Desconectado"
            }
        })
    }

    private fun configAutoAccepted() {
        config_auto_accept.setOnCheckedChangeListener { _, isChecked ->
            if (PreferencesManager.getConfigStoreAutoAccept() != isChecked) {
                try {
                    val dto = StoreSaturationChange()
                    dto.companyId = PreferencesManager.getCompanyId()
                    dto.status = isChecked
                    val register: Call<ResponseStoreAutoAccept> = apiClient!!.changeAcceptanceConfig(dto)
                    register.enqueue(object : Callback<ResponseStoreAutoAccept?> {
                        override fun onFailure(@Nullable call: Call<ResponseStoreAutoAccept?>?, @Nullable t: Throwable?) {
                            Log.e(LOG_TAG, t!!.message!!)
                            FileLog.writeToConsole(t!!.message!!)
                            config_auto_accept.isChecked = !isChecked
                            status_auto_accept!!.text = if (PreferencesManager.getConfigStoreAutoAccept()) "Auto aceptar" else "Aceptar manual"
                        }

                        override fun onResponse(call: Call<ResponseStoreAutoAccept?>, response: retrofit2.Response<ResponseStoreAutoAccept?>) {
                            if (response.isSuccessful) {
                                PreferencesManager.setConfigStoreAutoAccept(response.body()!!.autoAcceptance)
                                var leyend = if (response.body()!!.autoAcceptance!!) "activo" else "inactivo"
                                Toast.makeText(applicationContext, "Auto aceptar ${leyend}", Toast.LENGTH_LONG).show()
                                if (response.body()!!.autoAcceptance!! && ordersViewModel != null) {
                                    ordersViewModel!!.changeOrderStatusByStatus(OrderStatus.arriving.name, OrderStatus.accepted.name)
                                    notifyAdapterChanged()
                                }
                                if (layout_list_arriving != null) {
                                    layout_list_arriving!!.visibility = if (response.body()!!.autoAcceptance!!) View.GONE else View.VISIBLE
                                }
                            } else {
                                config_auto_accept.isChecked = !isChecked
                                FileLog.writeToConsole("${response!!.body()!!}")
                                Log.e(LOG_TAG, "${response!!.body()!!}")
                            }
                            status_auto_accept!!.text = if (PreferencesManager.getConfigStoreAutoAccept()) "Auto aceptar" else "Aceptar manual"
                        }
                    })
                } catch (ex: Exception) {
                    config_auto_accept.isChecked = !isChecked
                    status_auto_accept!!.text = if (PreferencesManager.getConfigStoreAutoAccept()) "Auto aceptar" else "Aceptar manual"
                    Log.e(LOG_TAG, ex.message!!)
                    FileLog.writeToConsole("$LOG_TAG - ${ex.message}")

                }
            }
        }
    }

    private fun configSaturated() {
        config_saturated.setOnCheckedChangeListener { _, isChecked ->
            if (PreferencesManager.getConfigStoreSaturated() != isChecked) {
                try {
                    val dto = StoreSaturationChange()
                    dto.companyId = PreferencesManager.getCompanyId()
                    dto.status = isChecked
                    val register: Call<ResponseStoreSaturated> = apiClient!!.changeSaturationConfig(dto)
                    register.enqueue(object : Callback<ResponseStoreSaturated?> {
                        override fun onFailure(@Nullable call: Call<ResponseStoreSaturated?>?, @Nullable t: Throwable?) {
                            config_saturated.isChecked = !isChecked
                            status_saturated!!.text = if (PreferencesManager.getConfigStoreSaturated()) "Saturar" else "Descargar"
                            Log.e(LOG_TAG, t!!.message!!)
                            FileLog.writeToConsole(t!!.message!!)
                        }

                        override fun onResponse(call: Call<ResponseStoreSaturated?>, response: retrofit2.Response<ResponseStoreSaturated?>) {
                            if (response.isSuccessful) {
                                PreferencesManager.setConfigStoreSaturated(response.body()!!.saturated)
                                var leyend = if (response.body()!!.saturated!!) "activo" else "inactivo"
                                Toast.makeText(applicationContext, "Saturado ${leyend}", Toast.LENGTH_LONG).show()
                                text_average_time_saturated!!.text = "${response.body()!!.avgtime} min"
                            } else {
                                config_saturated.isChecked = !isChecked
                                if (response != null) {
                                    FileLog.writeToConsole("${response!!}")
                                    Log.e(LOG_TAG, "${response!!.body()!!}")
                                }
                            }
                            status_saturated!!.text = if (PreferencesManager.getConfigStoreSaturated()) "Saturado" else "Descargado"
                        }
                    })
                } catch (ex: Exception) {
                    config_saturated.isChecked = !isChecked
                    status_saturated!!.text = if (PreferencesManager.getConfigStoreSaturated()) "Saturado" else "Descargado"
                    Log.e(LOG_TAG, ex.message!!)
                    FileLog.writeToConsole("$LOG_TAG - ${ex.message}")
                }
            }
        }
    }

    fun dpToPx(dp: Int): Int {
        val r: Resources = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).roundToInt()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(mBatInfoReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        super.onStart()
        if (!PreferencesManager.getLogoBanner().isNullOrEmpty()) {
            val options: RequestOptions = RequestOptions()
                    .placeholder(R.drawable.banner)
                    .error(R.drawable.banner)
            Glide.with(App.getAppInstance().applicationContext).load(
                    Constants.URL_BASE_ENFLASH + PreferencesManager.getLogoBanner()
            ).apply(options).into(store_banner)
        }

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                        permisionok = report.areAllPermissionsGranted()
                        Log.d("Permisos", "Permisos: " + report.areAllPermissionsGranted())

                        if (report.isAnyPermissionPermanentlyDenied) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, 101)
                        }

                        registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
                }).check()
    }

    override fun onResume() {
        super.onResume()
        validatePermission()
        notifyAdapterChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(data: StatusEvent?) {
        when (data!!.status) {
            AWSIotMqttClientStatus.Connected -> {
            }
            AWSIotMqttClientStatus.Connecting -> {
            }
            AWSIotMqttClientStatus.ConnectionLost -> {
            }
            AWSIotMqttClientStatus.Reconnecting -> {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(data: NewOrderEvent?) {
        notifyAdapterChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(data: OrderNotAssignedEvent?) {
        processOrderNotAssigned(data!!.orderStatus)
    }

    private fun processOrderNotAssigned(order: ResponseOrderStatus) {
        val orderDb = ordersViewModel!!.getOrderById(order.orderId!!)
        ShowNotification.sendAlertByOrderNotAssigned("La orden ${orderDb.folio} no fue asignada a un repartidor")
        Toast.makeText(applicationContext, "La orden ${orderDb.folio} no fue asignada a un repartidor", Toast.LENGTH_LONG).show()
    }

    private fun exit() {
        finish()
    }

    private fun signOut() {
        cognitoUser!!.signOut()
        exit()
    }

    private fun deleteAll() {
        modifiersViewModel!!.deleteAll()
        orderItemsViewModel!!.deleteAll()
        ordersViewModel!!.deleteAll()
        customAdapterAccepted!!.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.sesion) {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> signOut()
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
            val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.myDialog))
            builder.setTitle("Atención").setMessage("¿Estas seguro que deseas cerrar la sesion?").setPositiveButton("Aceptar", dialogClickListener)
                    .setNegativeButton("Cancelar", dialogClickListener).show()
            return true
        }
        if (id == R.id.deleteAll) {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> deleteAll()
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
            val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.myDialog))
            builder.setTitle("Atención").setMessage("¿Estas seguro que deseas borrar los datos?")
                    .setPositiveButton("Aceptar", dialogClickListener)
                    .setNegativeButton("Cancelar", dialogClickListener).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            val level = intent.getIntExtra("level", 0)
            batteryLevel = String.format("%s", level)
        }
    }

    private fun sendEmail(recipient: String, subject: String, message: String): Boolean {
        try {
            try {
                val file = File(PreferencesManager.getPath())
                val imageUri = FileProvider.getUriForFile(applicationContext, Constants.FILE_PROVIDER, file)
                val intentShareFile = Intent(Intent.ACTION_SEND)
                intentShareFile.data = Uri.parse("mailto:")
                intentShareFile.type = "text/*"
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, subject)
                intentShareFile.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                intentShareFile.putExtra(Intent.EXTRA_TEXT, message)
                intentShareFile.putExtra(Intent.EXTRA_STREAM, imageUri)
                startActivity(Intent.createChooser(intentShareFile, "Compartir con"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
        return true
    }

    companion object {
        val LOG_TAG = MainActivity::class.java.canonicalName
        var status: OrderStatus = OrderStatus.rejected
        var ordersArriving: ArrayList<com.enflash.mobile.storeapp.database.tableorders.Order> = ArrayList()
        var ordersAccepted: ArrayList<com.enflash.mobile.storeapp.database.tableorders.Order> = ArrayList()
        var ordersReady: ArrayList<com.enflash.mobile.storeapp.database.tableorders.Order> = ArrayList()
        var countDownTimerArriving: ConcurrentHashMap<String, CountDownTimer> = ConcurrentHashMap()
        var countDownTimerAccepted: ConcurrentHashMap<String, CountDownTimer> = ConcurrentHashMap()
        var customAdapterArriving: ListAdapter? = null
        var customAdapterAccepted: ListAdapter? = null
        var customAdapterReady: ListReadyAdapter? = null

        fun notifyAdapterChanged() {
            if (customAdapterArriving != null) {
                customAdapterArriving!!.notifyDataSetChanged()
            }
            if (customAdapterAccepted != null) {
                customAdapterAccepted!!.notifyDataSetChanged()
            }
            if (customAdapterReady != null) {
                customAdapterReady!!.notifyDataSetChanged()
            }
        }

    }

}