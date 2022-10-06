package com.enflash.mobile.storeapp.main.config.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.main.config.ConfiguracionActivity
import com.enflash.mobile.storeapp.main.config.models.Product
import com.enflash.mobile.storeapp.utils.Constants
import com.enflash.mobile.storeapp.utils.PreferencesManager

class ChildAdapter(private val children: List<Product>)
    : RecyclerView.Adapter<ChildAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.child_recycler_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val child = children[position]

        holder.folio.text = child.name!!
        holder.price.text = "${child.price}"
        holder.status.isChecked = child.enabled!!
        holder.statusConfigItem.text = if(child.enabled!!) "Activado" else "Desactivado"
        holder.status.setOnClickListener {
            child.enabled = holder.status.isChecked
            holder.statusConfigItem.text = if(child.enabled!!) "Activado" else "Desactivado"
            ConfiguracionActivity.postUpdate(PreferencesManager.getCompanyId(), child.id!!, holder.status.isChecked, position)
        }

        val options: RequestOptions = RequestOptions()
                .timeout(30000)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)

        Glide.with(App.getAppInstance().applicationContext).load(Constants.URL_BASE_ENFLASH + child.thumbnail).apply(options).into(holder.imagen)

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var folio: TextView
        var statusConfigItem: TextView
        var status: Switch
        var price: TextView
        var aceptar: Button
        var click: CardView
        var imagen: ImageView

        init {
            folio = view.findViewById(R.id.folio)
            statusConfigItem = view.findViewById(R.id.status_config_item)
            status = view.findViewById(R.id.action_review)
            price = view.findViewById(R.id.price)
            imagen = view.findViewById(R.id.imagen)
            aceptar = view.findViewById(R.id.action_review)
            click = view.findViewById(R.id.card_view)
        }
    }
}