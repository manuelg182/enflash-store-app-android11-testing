package com.enflash.mobile.storeapp.main.config

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.main.ApiClient
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.main.config.models.*
import com.enflash.mobile.storeapp.main.config.views.adapters.CategoriasAdapter
import com.enflash.mobile.storeapp.main.config.views.adapters.ParentAdapter
import com.enflash.mobile.storeapp.main.model.RequestPost
import com.enflash.mobile.storeapp.main.model.ResponsePost
import com.enflash.mobile.storeapp.utils.Constants
import com.enflash.mobile.storeapp.utils.CustomProgress
import com.enflash.mobile.storeapp.utils.PreferencesManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import retrofit2.Call
import retrofit2.Callback


class ConfiguracionActivity : AppCompatActivity() {

    var apiClient: ApiClient? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var searchView: SearchView? = null
    private var progress = CustomProgress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        initCollapsingToolbar()

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh)
        productoslist = findViewById(R.id.productos)
        categorialist = findViewById(R.id.categorias)
        searchView = findViewById(R.id.search)

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // hideKeyboard()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query!!.isNotEmpty()) {
                    adapterProductos!!.filter.filter(query)
                } else {
                    getProducts()
                }
                return false
            }

        })

        mSwipeRefreshLayout!!.setOnRefreshListener {
            getProducts()
            mSwipeRefreshLayout!!.isRefreshing = false
        }

        try {
            val options: RequestOptions = RequestOptions()
                    .timeout(5000)
                    .placeholder(R.drawable.banner)
                    .error(R.drawable.banner)
            Glide.with(applicationContext).load(
                    Constants.URL_BASE_ENFLASH + PreferencesManager.getLogoBanner()
            ).apply(options).into(findViewById<ImageView>(R.id.store_banner))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        apiClient = ApiClient(this)

        getSections()

    }

    private fun hide() {
        if (progress.dialog != null) {
            if (progress.dialog!!.isShowing) {
                progress.dialog!!.dismiss()
            }
        }
    }

    private fun getSections() {

        progress.show(this@ConfiguracionActivity, false)

        val sections: Call<List<Section>> = apiClient!!.getSections(PreferencesManager.getCompanyId())

        sections.enqueue(object : Callback<List<Section>?> {

            override fun onFailure(@Nullable call: Call<List<Section>?>?, @Nullable t: Throwable?) {
                t?.printStackTrace()
                hide()
            }

            override fun onResponse(call: Call<List<Section>?>, response: retrofit2.Response<List<Section>?>) {
                if (response.isSuccessful) {

                    listSections = response.body()

                    var listendcategorias = arrayListOf<Section>()

                    for (item in listSections!!) {
                        if (item.enabled!!) {
                            listendcategorias.add(item)
                        }
                    }

                    adapterCategorias = CategoriasAdapter(listendcategorias!!)

                    categorialist!!.apply {
                        layoutManager = LinearLayoutManager(this@ConfiguracionActivity, RecyclerView.HORIZONTAL, false)
                        adapter = adapterCategorias
                    }

                    getProducts()
                }
                hide()
            }
        })

    }

    private fun getProducts() {

        val product: Call<List<Product>> = apiClient!!.getProducts(PreferencesManager.getCompanyId())

        product.enqueue(object : Callback<List<Product>?> {

            override fun onFailure(@Nullable call: Call<List<Product>?>?, @Nullable t: Throwable?) {
                t?.printStackTrace()
                hide()
            }

            override fun onResponse(call: Call<List<Product>?>, response: retrofit2.Response<List<Product>?>) {
                if (response.isSuccessful) {
                    Log.d(MainActivity.LOG_TAG, "Message: " + response.body())
                    listProductos = response.body()

                    val parents = mutableListOf<ParentModel>()

                    for (child in listSections!!) {
                        var lastitemsproductos = arrayListOf<Product>()
                        if (child.productIds != null && child.productIds!!.isNotEmpty()) {
                            for (item in child.productIds!!) {
                                for (prod in listProductos!!) {
                                    if (prod.id == item) {
                                        lastitemsproductos.add(prod)
                                    }
                                }
                            }
                        }
                        if (lastitemsproductos.isNotEmpty() || !child.enabled!!) {
                            parents.add(ParentModel(child, lastitemsproductos))
                        }
                    }

                    adapterProductos = ParentAdapter(parents)
                    productoslist!!.apply {
                        layoutManager = LinearLayoutManager(this@ConfiguracionActivity, RecyclerView.VERTICAL, false)
                        adapter = adapterProductos
                    }
                    hide()
                }
                hide()
            }
        })

    }

    companion object {

        var listSections: List<Section>? = null
        var listProductos: List<Product>? = null
        var apiClient = ApiClient(App.getAppInstance().applicationContext)
        var categorialist: RecyclerView? = null
        var productoslist: RecyclerView? = null
        var adapterProductos: ParentAdapter? = null
        var adapterCategorias: CategoriasAdapter? = null

        fun postUpdate(id: Long, productId: Long, enabled: Boolean, position: Int) {

            val dto = RequestPost()
            dto.enabled = enabled
            val productos: Call<ResponsePost> = apiClient.updateStatus(id, productId, dto)
            productos.enqueue(object : Callback<ResponsePost?> {
                override fun onFailure(@Nullable call: Call<ResponsePost?>?, @Nullable t: Throwable?) {
                    adapterProductos!!.notifyItemChanged(position)
                    Toast.makeText(App.getAppInstance().applicationContext, "No se actualizo", Toast.LENGTH_SHORT).show()
                    adapterProductos!!.notifyItemChanged(position)
                }

                override fun onResponse(call: Call<ResponsePost?>, response: retrofit2.Response<ResponsePost?>) {
                    if (response.isSuccessful) {
                        Toast.makeText(App.getAppInstance().applicationContext, "Actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        fun postUpdateSection(id: Long, productId: Long, enabled: Boolean, position: Int) {

            val dto = RequestPost()
            dto.enabled = enabled
            val productos: Call<ResponsePost> = apiClient.updateStatusSection(id, productId, dto)
            productos.enqueue(object : Callback<ResponsePost?> {
                override fun onFailure(@Nullable call: Call<ResponsePost?>?, @Nullable t: Throwable?) {
                    adapterProductos!!.notifyItemChanged(position)
                    Toast.makeText(App.getAppInstance().applicationContext, "No se actualizo", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ResponsePost?>, response: retrofit2.Response<ResponsePost?>) {
                    if (response.isSuccessful) {
                        Toast.makeText(App.getAppInstance().applicationContext, "Actualizado", Toast.LENGTH_SHORT).show()
                        adapterProductos!!.notifyItemChanged(position)
                    }
                }
            })
        }

        fun getFilterList(section: Section) {

            val parents = mutableListOf<ParentModel>()

            var lastitemsproductos = arrayListOf<Product>()
            if (section.productIds != null && section.productIds!!.isNotEmpty()) {
                for (item in section.productIds!!) {
                    for (prod in listProductos!!) {
                        if (prod.id == item) {
                            lastitemsproductos.add(prod)
                        }
                    }
                }
            }
            parents.add(ParentModel(section, lastitemsproductos))

            adapterProductos = ParentAdapter(parents)

            productoslist!!.apply {
                layoutManager = LinearLayoutManager(App.getAppInstance().applicationContext, RecyclerView.VERTICAL, false)
                adapter = adapterProductos
            }
        }
    }


    private fun initCollapsingToolbar() {
        val collapsingToolbar: CollapsingToolbarLayout = findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbar.title = " "
        val appBarLayout: AppBarLayout = findViewById<View>(R.id.appbar) as AppBarLayout
        appBarLayout.setExpanded(true)

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = getString(R.string.app_name)
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })
    }

}