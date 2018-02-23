package nathan.com.practica_qr_kotlin.helper


import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View

import nathan.com.practica_qr_kotlin.AdaptadorRVFavoritos

class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val listener: RecyclerItemTouchHelperListener?) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (listener != null) {
            listener!!.onSwipe(viewHolder, direction, viewHolder.adapterPosition)
        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
        val viewFG = (viewHolder as AdaptadorRVFavoritos.AdaptadorViewHolder).viewFG
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(viewFG)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val viewFG = (viewHolder as AdaptadorRVFavoritos.AdaptadorViewHolder).viewFG
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(viewFG)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val viewFG = (viewHolder as AdaptadorRVFavoritos.AdaptadorViewHolder).viewFG
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, viewFG, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val viewFG = (viewHolder as AdaptadorRVFavoritos.AdaptadorViewHolder).viewFG
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, viewFG, dX, dY, actionState, isCurrentlyActive)
    }
}