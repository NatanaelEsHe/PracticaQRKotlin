package nathan.com.practica_qr_kotlin


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import java.util.ArrayList


class AdaptadorVPMain(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val arrayFrag: ArrayList<Fragment>
    private val arrayTitle: ArrayList<String>

    init {
        arrayFrag = ArrayList()
        arrayTitle = ArrayList()
    }

    override fun getItem(position: Int): Fragment {
        return arrayFrag[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return arrayTitle[position]
    }

    override fun getCount(): Int {
        return arrayTitle.size
    }

    fun addFragment(frag: Fragment, title: String) {
        arrayFrag.add(frag)
        arrayTitle.add(title)
    }
}