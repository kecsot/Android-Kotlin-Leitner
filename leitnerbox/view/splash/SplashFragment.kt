package com.kecsot.leitnerbox.view.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.R

class SplashFragment : AbstractFragment() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = AbstractViewModel.get(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onSubscribeViewModelObservables() {
        viewModel.run {
            onSplashLoadedPublishSubject.observe(this@SplashFragment, Observer {
                navigateToMain()
            })
        }
    }

    private fun navigateToMain() {
        val direction = SplashFragmentDirections.actionSplashFragmentToMainFragment()

        navigation.navigate(direction)
    }

    override fun onStart() {
        super.onStart()
        hideActionBar()
        viewModel.loadSplash()
    }

}