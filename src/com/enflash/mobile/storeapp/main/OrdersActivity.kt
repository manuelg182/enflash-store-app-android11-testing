package com.enflash.mobile.storeapp.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.main.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class OrdersActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.viewpager)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = "Recolectadas"

        setupViewPager(viewPager!!)
        tabLayout!!.setupWithViewPager(viewPager!!)

        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                invalidateOptionsMenu()
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        setupTabIcons()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    }

    private fun setupTabIcons() {
        if (tabLayout != null) {
            tabLayout!!.getTabAt(0)!!.setText("Recolectadas").setIcon(R.drawable.ic_logo)
            tabLayout!!.getTabAt(1)!!.setText("Entregadas").setIcon(R.drawable.ic_logo)
            tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val actionBar = supportActionBar
                    when (tab.position) {
                        0 -> actionBar?.setTitle("Recolectadas")

                        1 -> actionBar?.setTitle("Entregadas")
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }


}