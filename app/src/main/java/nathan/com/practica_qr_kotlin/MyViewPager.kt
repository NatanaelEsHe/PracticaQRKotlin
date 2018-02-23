package nathan.com.practica_qr_kotlin

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class MyViewPager : ViewPager {

    private var swipe = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (!swipe) {
            false
        } else super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (!swipe) {
            false
        } else super.onInterceptTouchEvent(event)
    }

    fun setEnabledSwipe(enabled: Boolean) {
        swipe = enabled
    }

}