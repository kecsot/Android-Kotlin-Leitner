package com.kecsot.leitnerbox.common.imageviewpager

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.kecsot.leitnerbox.R
import kotlinx.android.synthetic.main.listitem_imageviewpager.view.*


class ImageViewPagerAdapter(val context: Context, val images: ArrayList<Uri>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.listitem_imageviewpager, container, false)

        itemView.listitem_imageviewpager_image.setImageURI(images[position])

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return images.size
    }

}