package com.enflash.mobile.storeapp.main.config.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.main.config.ConfiguracionActivity
import com.enflash.mobile.storeapp.main.config.models.ParentModel
import com.enflash.mobile.storeapp.utils.PreferencesManager
import kotlinx.android.synthetic.main.parent_header_recycler.view.*
import kotlinx.android.synthetic.main.parent_recycler.view.rv_child
import java.util.*
import kotlin.collections.ArrayList

class ParentAdapter(val productos: List<ParentModel>) : RecyclerView.Adapter<ParentAdapter.ViewHolder>(), Filterable {

    var proudctosFilterList = arrayListOf<ParentModel>()

    init {
        proudctosFilterList.addAll(productos)
    }

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.parent_header_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return proudctosFilterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = proudctosFilterList[position]

        holder.textView.text = parent.section.name
        holder.status.isChecked = parent.section.enabled!!

        holder.status.setOnClickListener {
            parent.section.enabled = holder.status.isChecked
            ConfiguracionActivity.postUpdateSection(PreferencesManager.getCompanyId(), parent.section.id!!, holder.status.isChecked, position)
        }

        holder.recyclerView.apply {
            layoutManager = LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
            adapter = ChildAdapter(parent.children)
            setRecycledViewPool(viewPool)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.rv_child
        val textView: TextView = itemView.title_order
        val status: Switch = itemView.status_config_item
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    proudctosFilterList.addAll(productos)
                } else {
                    val resultList = ArrayList<ParentModel>()
                    for (row in productos) {
                        if (row.section.name!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    proudctosFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = proudctosFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                proudctosFilterList = results?.values as ArrayList<ParentModel>
                notifyDataSetChanged()
            }

        }
    }

}