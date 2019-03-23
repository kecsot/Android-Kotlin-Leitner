package com.kecsot.leitnerbox.common.infoview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.kecsot.leitnerbox.R
import kotlinx.android.synthetic.main.view_info.view.*

class InfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_info, this, true)
    }

    public fun setTitle(value: String) {
        view_info_title.text = value
    }

    public fun setTitle(@StringRes id: Int) {
        setTitle(context.getString(id))
    }

    public fun setMessage(value: String) {
        view_info_message.text = value
    }

    public fun setMessage(@StringRes id: Int) {
        setMessage(context.getString(id))
    }

}