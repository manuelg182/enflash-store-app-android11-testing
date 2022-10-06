package com.enflash.mobile.storeapp.ordenes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItem
import kotlinx.android.synthetic.main.child_recycler.view.*
import java.text.DecimalFormat

class ChildAdapter(private val children: List<ModifierItem>)
    : RecyclerView.Adapter<ChildAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.child_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val child = children[position]

        if (child.type.toLowerCase() == "extras") {
            if(child.quantity > 1){
                holder.textViewDesc.text = "${child.quantity} ${child.name}"
            }else{
                holder.textViewDesc.text = "${child.name}"
            }
            var decimal = DecimalFormat("00.00")
            holder.textViewPrice.text = "$${decimal.format(child.price)}"
        } else {
            if(child.quantity > 1){
                holder.textViewDesc.text = "${child.quantity} ${child.name}"
            }else{
                holder.textViewDesc.text = "${child.name}"
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDesc: TextView = itemView.desc
        val textViewPrice: TextView = itemView.price
    }
}