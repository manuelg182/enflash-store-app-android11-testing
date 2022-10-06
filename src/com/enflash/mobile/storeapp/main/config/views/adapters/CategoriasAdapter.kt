package com.enflash.mobile.storeapp.main.config.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.main.config.ConfiguracionActivity
import com.enflash.mobile.storeapp.main.config.models.Section
import kotlinx.android.synthetic.main.parent_categorias.view.*

class CategoriasAdapter(private val parents: List<Section>) : RecyclerView.Adapter<CategoriasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.parent_categorias, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = parents[position]
        holder.textView.text = parent.name
        holder.textView.setOnClickListener {
            ConfiguracionActivity.getFilterList(parent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.textView
    }
}