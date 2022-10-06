package com.enflash.mobile.storeapp.ordenes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemViewModel
import com.enflash.mobile.storeapp.database.tableorders.Order
import com.enflash.mobile.storeapp.database.tableproductos.OrderItem
import com.enflash.mobile.storeapp.utils.Utilities
import kotlinx.android.synthetic.main.parent_recycler.view.*

class ParentAdapter(private val parents: List<OrderItem>, var order: Order, var live: LifecycleOwner, var modifierItemViewModel: ModifierItemViewModel) : RecyclerView.Adapter<ParentAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.parent_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        val parent = parents[position]
        holder.textId.text = "${parent.quantity}"
        holder.textTitle.text = parent.description
        holder.textPrice.text = Utilities.formatter(parent.price)
        holder.textTotal.text = Utilities.formatter(parent.total)

        val childLayoutManager = LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
        holder.recyclerView.apply {
            var lista = modifierItemViewModel.getModifiers(parent.uuid)

            layoutManager = childLayoutManager
            adapter = ChildAdapter(lista)
            setRecycledViewPool(viewPool)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.rv_child as RecyclerView
        val textId: TextView = itemView.txtid
        val textTitle: TextView = itemView.title_order
        val textPrice: TextView = itemView.precio
        val textTotal: TextView = itemView.total
    }
}