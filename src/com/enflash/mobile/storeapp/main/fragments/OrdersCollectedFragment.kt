package com.enflash.mobile.storeapp.main.fragments

import android.os.Bundle
import android.util.Log
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
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.main.adapter.ListReadyAdapter
import java.util.*


class OrdersCollectedFragment : Fragment() {

    private var ordersViewModel: OrderViewModel? = null
    private var orderItemsViewModel: OrderItemViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var customAdapterCollected: ListReadyAdapter? = null
    private var ordersCollected: ArrayList<com.enflash.mobile.storeapp.database.tableorders.Order> = ArrayList()
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
            customAdapterCollected!!.notifyDataSetChanged()
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

        customAdapterCollected = ListReadyAdapter(ordersCollected, v.context, orderItemsViewModel!!)
        recyclerView!!.adapter = customAdapterCollected

        var observer = androidx.lifecycle.Observer<List<com.enflash.mobile.storeapp.database.tableorders.Order>> { mesas ->
            if (mesas != null) {
                ordersCollected.clear()
                ordersCollected.addAll(mesas)
                customAdapterCollected!!.notifyDataSetChanged()
            }
        }

        ordersViewModel!!.ordersCollected.observe(requireActivity(), observer)
        return v
    }

}