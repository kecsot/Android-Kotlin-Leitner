package com.kecsot.leitnerbox

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.view.AbstractAppCompatActivity
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.app_bar_main.*


class SingleActivity : AbstractAppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        setSupportActionBar(toolbar)
    }

    override fun setupFloatingActionButton(isVisible: Boolean, drawableRes: Int?) {
        fab?.run {
            visibility = if (isVisible) View.VISIBLE else View.GONE

            drawableRes?.let {
                setImageDrawable(AppCompatResources.getDrawable(context, it))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupFloatingActionButtonClick(subject: PublishSubject<Unit>) {
        fab.clicks()
            .subscribe(subject)
    }

}
