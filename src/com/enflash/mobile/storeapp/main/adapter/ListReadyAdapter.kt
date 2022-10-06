package com.enflash.mobile.storeapp.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tableorders.Order
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemViewModel
import com.enflash.mobile.storeapp.main.OrderDetailActivity
import com.enflash.mobile.storeapp.main.adapter.ListReadyAdapter.MyViewReadyHolder
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.enflash.mobile.storeapp.utils.Utilities
import java.util.*


class ListReadyAdapter(private val orders: ArrayList<Order>, var context: Context,
                       var productosViewModel: OrderItemViewModel) : RecyclerView.Adapter<MyViewReadyHolder>() {

    inner class MyViewReadyHolder(view: View) : RecyclerView.ViewHolder(view) {

        var folio: TextView
        var price: TextView
        var cantidad: TextView
        var review: Button
        var cardView: CardView
        var listItemCircle: ImageView

        init {
            folio = view.findViewById(R.id.folio)
            price = view.findViewById(R.id.price)
            cantidad = view.findViewById(R.id.cantidad)
            review = view.findViewById(R.id.action_review)
            cardView = view.findViewById(R.id.card_view)
            listItemCircle = view.findViewById(R.id.list_item_circle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewReadyHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_ready, parent, false)
        return MyViewReadyHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewReadyHolder, position: Int) {
        val item = orders[position]
        holder.folio.text = item.folio
        val count = productosViewModel!!.countProducts(item.orderId)
        var leyendaProductos = "producto"
        if (count > 0) {
            leyendaProductos = "productos"
        }
        holder.cantidad.text = "${count} ${leyendaProductos}"
        holder.price.text = Utilities.formatter(item!!.total)
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

    override fun getItemCount(): Int {
        return orders.size
    }
}