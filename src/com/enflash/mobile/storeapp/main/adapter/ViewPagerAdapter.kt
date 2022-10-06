package com.enflash.mobile.storeapp.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.enflash.mobile.storeapp.main.fragments.OrdersCollectedFragment
import com.enflash.mobile.storeapp.main.fragments.OrdersDeliveredFragment


class ViewPagerAdapter constructor(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    private var childFragments: Array<Fragment>

    init {
        childFragments = arrayOf(
                OrdersCollectedFragment(),
                OrdersDeliveredFragment()
        )
    }

    override fun getItem(position: Int): Fragment {
        return childFragments[position]
    }

    override fun getCount(): Int {
        return childFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val title = getItem(position).javaClass.name
        return title.subSequence(title.lastIndexOf(".") + 1, title.length).toString()
    }
}

