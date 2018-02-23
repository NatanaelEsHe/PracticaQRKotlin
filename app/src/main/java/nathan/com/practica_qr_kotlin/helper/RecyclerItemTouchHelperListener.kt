package nathan.com.practica_qr_kotlin.helper

import android.support.v7.widget.RecyclerView

interface RecyclerItemTouchHelperListener {
    fun onSwipe(viewHolder: RecyclerView.ViewHolder, dir: Int, position: Int)
}