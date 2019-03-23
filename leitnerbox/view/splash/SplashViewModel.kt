package com.kecsot.leitnerbox.view.splash

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SplashViewModel : AbstractViewModel() {

    public val onSplashLoadedPublishSubject = MutableLiveData<Boolean>()

    private val SPLASH_MINIMUM_LOADING_TIME = 2200L

    @SuppressLint("CheckResult")
    public fun loadSplash() {
        Single.timer(SPLASH_MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onSplashLoadedPublishSubject.postValue(true)
            }, {
                Timber.e(it)
            })
            .addToCompositeDisposable()
    }

}