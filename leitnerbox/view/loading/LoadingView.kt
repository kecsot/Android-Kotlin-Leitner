package com.kecsot.leitnerbox.view.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.kecsot.leitnerbox.R

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        isEnabled = false
        isClickable = false
        LayoutInflater.from(context).inflate(R.layout.view_loading, this, true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

}