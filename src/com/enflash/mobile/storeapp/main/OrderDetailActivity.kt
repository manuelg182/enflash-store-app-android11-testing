package com.enflash.mobile.storeapp.main

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemViewModel
import com.enflash.mobile.storeapp.database.tableorders.Order
import com.enflash.mobile.storeapp.database.tableorders.OrderViewModel
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemViewModel
import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus
import com.enflash.mobile.storeapp.main.model.ResponseStore
import com.enflash.mobile.storeapp.ordenes.adapter.ParentAdapter
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.CustomProgress
import com.enflash.mobile.storeapp.utils.FileLog
import com.enflash.mobile.storeapp.utils.PreferencesManager
import com.enflash.mobile.storeapp.utils.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_order_detail.*
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    var ordersViewModel: OrderViewModel? = null
    var productosViewModel: OrderItemViewModel? = null
    var modifierItemViewModel: ModifierItemViewModel? = null
    var order: Order? = null
    var apiClient: ApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        apiClient = ApiClient(this)
        var owner = this
        productosViewModel = ViewModelProvider(owner).get(OrderItemViewModel::class.java)
        ordersViewModel = ViewModelProvider(owner).get(OrderViewModel::class.java)
        modifierItemViewModel = ViewModelProvider(owner).get(ModifierItemViewModel::class.java)

        var data = intent.extras

        val orderId = data!!.getString("orderId")
        this.order = ordersViewModel!!.getOrderById(orderId!!)

        val status = this.order!!.status

        if (status == OrderStatus.arriving.name) {
            action_review.text = "Aceptar"
            action_reject.text = "Rechazar"
        } else if (status == OrderStatus.accepted.name) {
            action_review.text = "Recolectar"
            action_reject.text = "Cancelar"
        } else if (status == OrderStatus.ready.name) {
            action_review.visibility = View.GONE
            action_reject.visibility = View.GONE
        }

        action_review.setOnClickListener {
            if (status == OrderStatus.accepted.name) {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            if (PreferencesManager.getConfigStoreOperationStatus()) {
                                requestOrderReadyToCollect()
                            } else {
                                Toast.makeText(applicationContext, "Debe iniciar operaciones para aceptar o rechazar ordenes", Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
                val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.myDialog))
                builder.setTitle("Atención").setMessage("Pedido listo para envío, ¿desea continuar?")
                        .setPositiveButton("Aceptar", dialogClickListener)
                        .setNegativeButton("Cancelar", dialogClickListener)
                        .show()
            } else {
                if (PreferencesManager.getConfigStoreOperationStatus()) {
                    requestAcceptNewOrderRequest()
                } else {
                    Toast.makeText(applicationContext, "Debe iniciar operaciones para aceptar o rechazar ordenes", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        action_reject.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        if (PreferencesManager.getConfigStoreOperationStatus()) {
                            requestOrderRejectNewRequest()
                        } else {
                            Toast.makeText(applicationContext, "Debe iniciar operaciones para aceptar o rechzar ordenes", Toast.LENGTH_LONG).show()
                        }
                        finish()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.myDialog))
            builder.setTitle("Atención").setMessage("Se cancelará el pedido, ¿desea continuar?")
                    .setPositiveButton("Aceptar", dialogClickListener)
                    .setNegativeButton("Cancelar", dialogClickListener)
                    .show()
        }

        title_order!!.text = "Folio: ${this.order!!.folio}"
        total!!.text = Utilities.formatter(this.order!!.total)
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date: String = simpleDateFormat.format(Date(this.order!!.createdAt))
        subtitulo!!.text = "Ordenado a las $date horas"
        preparation_time!!.text = "Tiempo de preparación: ${this.order!!.averageTime} minutos"
        order_detail_notes.text = order!!.notes
        var lista = productosViewModel!!.getItemsByOrderId(this.order!!.orderId)

        var orderB = this.order
        if (lista.isNotEmpty()) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@OrderDetailActivity, RecyclerView.VERTICAL, false)
                adapter = ParentAdapter(lista, orderB!!, owner, modifierItemViewModel!!)
            }
        }
    }

    private fun requestAcceptNewOrderRequest() {
        val dto = ResponseOrderStatus()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.status = OrderStatus.accepted
        dto.orderId = order!!.orderId
        val register: Call<ResponseOrderStatus> = apiClient!!.acceptNewOrderRequest(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
                Toast.makeText(applicationContext, "Ocurrio un error al rechazar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (response.isSuccessful) {
                    ordersViewModel!!.updateOrderStatus(order!!.orderId, OrderStatus.accepted.name)
                    MainActivity.notifyAdapterChanged()
                    Toast.makeText(applicationContext, "Orden ${order!!.folio} aceptada", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "Ocurrio un error al acceptar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                    FileLog.writeToConsole("Ocurrio un error al aceptar la orden ${order!!.folio}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
                finish()
            }
        })
    }

    private fun requestOrderReadyToCollect() {
        val dto = ResponseStore()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.datetime = Date().time / 1000
        dto.status = OrderStatus.ready
        dto.orderId = order!!.orderId
        val register: Call<ResponseOrderStatus> = apiClient!!.orderReadyToCollect(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
                Toast.makeText(applicationContext, "Ocurrio un error al rechazar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (response.isSuccessful) {
                    ordersViewModel!!.updateOrderStatus(order!!.orderId, response.body()!!.status!!.name)
                    MainActivity.notifyAdapterChanged()
                    Toast.makeText(applicationContext, "Orden ${order!!.folio} actualizada", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "Ocurrio un error al actualizar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                    FileLog.writeToConsole("Ocurrio un error al actualizar (ready) la orden ${order!!.folio}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
                finish()
            }
        })
    }

    private fun requestOrderRejectNewRequest() {
        val dto = ResponseStore()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.datetime = Date().time / 1000
        dto.status = OrderStatus.rejected
        dto.orderId = order!!.orderId
        dto.source = "user"
        ordersViewModel!!.updateOrderStatus(order!!.orderId, OrderStatus.rejected.name)
        MainActivity.notifyAdapterChanged()
        val register: Call<ResponseOrderStatus> = apiClient!!.orderRejectNewRequest(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
                Toast.makeText(applicationContext, "Ocurrio un error al rechazar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "La orden ${order!!.folio} fue rechazada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Ocurrio un error al rechazar la orden ${order!!.folio}", Toast.LENGTH_LONG).show()
                    FileLog.writeToConsole("Ocurrio un error al rechazar la orden ${order!!.folio}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
                finish()
            }
        })
    }

}