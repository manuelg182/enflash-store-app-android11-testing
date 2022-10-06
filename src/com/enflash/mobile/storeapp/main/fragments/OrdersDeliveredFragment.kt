package com.enflash.mobile.storeapp.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.database.tableorders.OrderViewModel
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemViewModel
import com.enflash.mobile.storeapp.main.adapter.ListReadyAdapter
import java.util.*


class OrdersDeliveredFragment : Fragment() {

    private var ordersViewModel: OrderViewModel? = null
    private var orderItemsViewModel: OrderItemViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var customAdapterDelivered: ListReadyAdapter? = null
    private var ordersDelivered: ArrayList<com.enflash.mobile.storeapp.database.tableorders.Order> = ArrayList()
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_orders, container, false)

        swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout!!.setOnRefreshListener {
            customAdapterDelivered!!.notifyDataSetChanged()
            swipeRefreshLayout!!.isRefreshing = false
        }

        ordersViewModel = run {
            ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
        }

        orderItemsViewModel = run {
            ViewModelProvider(requireActivity()).get(OrderItemViewModel::class.java)
        }

        recyclerView = v.findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        customAdapterDelivered = ListReadyAdapter(ordersDelivered, v.context, orderItemsViewModel!!)
        recyclerView!!.adapter = customAdapterDelivered

        var observer = androidx.lifecycle.Observer<List<com.enflash.mobile.storeapp.database.tableorders.Order>> { mesas ->
            if (mesas != null) {
                ordersDelivered.clear()
                ordersDelivered.addAll(mesas)
                customAdapterDelivered!!.notifyDataSetChanged()
            }
        }

        ordersViewModel!!.ordersDelivered.observe(requireActivity(), observer)
        return v
    }

}