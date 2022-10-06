package com.enflash.mobile.storeapp.main.adapter

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tableorders.Order
import com.enflash.mobile.storeapp.database.tableorders.OrderViewModel
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemViewModel
import com.enflash.mobile.storeapp.main.ApiClient
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.main.OrderDetailActivity
import com.enflash.mobile.storeapp.main.adapter.ListAdapter.MyViewHolder
import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus
import com.enflash.mobile.storeapp.main.model.ResponseStore
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.FileLog
import com.enflash.mobile.storeapp.utils.PreferencesManager
import com.enflash.mobile.storeapp.utils.ShowNotification
import com.enflash.mobile.storeapp.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import java.util.*


class ListAdapter(private val orders: ArrayList<Order>, var context: Context,
                  var orderViewModel: OrderViewModel,
                  var productosViewModel: OrderItemViewModel,
                  var apiClient: ApiClient) : RecyclerView.Adapter<MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var folio: TextView
        var description: TextView
        var time: TextView
        var price: TextView
        var cantidad: TextView
        var review: Button
        var cardView: CardView
        var listItemCircle: ImageView

        init {
            folio = view.findViewById(R.id.folio)
            description = view.findViewById(R.id.description)
            price = view.findViewById(R.id.price)
            time = view.findViewById(R.id.time)
            cantidad = view.findViewById(R.id.cantidad)
            review = view.findViewById(R.id.action_review)
            cardView = view.findViewById(R.id.card_view)
            listItemCircle = view.findViewById(R.id.list_item_circle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = orders[position]
        holder.folio.text = item.folio
        val count = productosViewModel!!.countProducts(item.orderId)
        var leyendaProductos = "producto"
        if (count > 0) {
            leyendaProductos = "productos"
        }
        holder.cantidad.text = "${count} ${leyendaProductos}"
        holder.price.text = Utilities.formatter(item!!.total)

        if (item.status == OrderStatus.accepted.name) {
            var diff: Long = (item.createdAt + item.averageTime * 60000) - Date().time
            diff = if (diff < 0) 0 else diff

            if (MainActivity.countDownTimerAccepted!!.containsKey(item.orderId)) {
                MainActivity.countDownTimerAccepted!![item.orderId]!!.cancel()
            }
            MainActivity.countDownTimerAccepted!![item.orderId] = object : CountDownTimer(diff, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60

                    when {
                        hours > 0 -> {
                            holder.listItemCircle.alpha = 0.1f
                            holder.time.text = "$hours"
                            holder.description.text = "Horas"
                        }
                        minutes > 0 -> {
                            holder.listItemCircle.alpha = 0.1f
                            holder.time.text = "$minutes"
                            holder.description.text = "Minutos"
                        }
                        seconds > 0 -> {
                            holder.listItemCircle.alpha = 0.1f
                            holder.time.text = "$minutes"
                            holder.description.text = "Minutos"
                        }
                    }
                }

                override fun onFinish() {
                    holder.listItemCircle.alpha = 0.6f
                    holder.time.text = "Atraso"
                    holder.description.text = ""
                }
            }
            MainActivity.countDownTimerAccepted!![item.orderId]!!.start()
        } else if (item.status == OrderStatus.arriving.name) {
            var diff: Long = (item.createdAt + (PreferencesManager.getTimeToReject() + 5) * 60000) - Date().time
            diff = if (diff < 0) 0 else diff
            if (MainActivity.countDownTimerArriving!!.containsKey(item.orderId)) {
                MainActivity.countDownTimerArriving!![item.orderId]!!.cancel()
            }
            MainActivity.countDownTimerArriving!![item.orderId] = object : CountDownTimer(diff, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    var diff: Long = Date().time - item.createdAt
                    diff = if (diff < 0) 0 else diff
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    if (orderViewModel!!.existByOrderIdAndStatus(item.orderId, OrderStatus.accepted.name)) {
                        this.cancel()
                    }

                    when {
                        hours > 0 -> {
                            holder.listItemCircle.alpha = 0.1f
                            holder.time.text = "$hours"
                            holder.description.text = "Horas"
                        }
                        minutes > 0 -> {
                            val time = PreferencesManager.getTimeToReject()
                            if (minutes in 2L..time) {
                                holder.time.text = "$minutes"
                                holder.listItemCircle.alpha = if (holder.listItemCircle.alpha == 0.1f) 0.9f else 0.1f
                                holder.description.text = "Minutos"
                            } else if (minutes > time) {
                                ShowNotification.sendAlertByOrderNotAssigned("La orden ${item.folio} fue auto-rechazada por falta de atención y aceptación")
                                Toast.makeText(context, "La orden ${item.folio} fue auto-rechazada por falta de atención y aceptación", Toast.LENGTH_LONG).show()
                                requestOrderRejectNewRequest(item.orderId)
                                this.cancel()
                            } else {
                                holder.listItemCircle.alpha = 0.1f
                                holder.time.text = "$minutes"
                                holder.description.text = "Minutos"
                            }
                        }
                        seconds > 0 -> {
                            holder.listItemCircle.alpha = 0.1f
                            holder.time.text = "$seconds"
                            holder.description.text = "segundos"
                        }
                    }

                }

                override fun onFinish() {
                    holder.listItemCircle.alpha = 0.9f
                    holder.time.text = "Alerta"
                    holder.description.text = ""
                }
            }
            MainActivity.countDownTimerArriving!![item.orderId]!!.start()
        }

        holder.review.setOnClickListener {
            var intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderId", item.orderId)
            if (item.status == OrderStatus.accepted.name) {
                intent.putExtra("active", true)
            } else {
                intent.putExtra("active", false)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun requestOrderRejectNewRequest(orderId: String) {
        val dto = ResponseStore()
        dto.companyId = PreferencesManager.getCompanyId()
        dto.datetime = Date().time / 1000
        dto.status = OrderStatus.rejected
        dto.orderId = orderId
        dto.source = "system"
        orderViewModel!!.updateOrderStatus(orderId, OrderStatus.rejected.name)
        MainActivity.notifyAdapterChanged()
        val register: Call<ResponseOrderStatus> = apiClient!!.orderRejectNewRequest(dto)
        register.enqueue(object : Callback<ResponseOrderStatus?> {
            override fun onFailure(@Nullable call: Call<ResponseOrderStatus?>?, @Nullable t: Throwable?) {
                Log.e(MainActivity.LOG_TAG, t!!.message!!)
                FileLog.writeToConsole(t!!.message!!)
            }

            override fun onResponse(call: Call<ResponseOrderStatus?>, response: retrofit2.Response<ResponseOrderStatus?>) {
                if (!response.isSuccessful) {
                    FileLog.writeToConsole("Ocurrio un error al rechazar la orden ${orderId}\n" +
                            "${response!!.code()} ${response!!.message()}")
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}