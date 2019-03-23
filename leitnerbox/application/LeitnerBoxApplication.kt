package com.kecsot.leitnerbox.application

import com.google.firebase.FirebaseApp
import com.kecsot.basekecsot.BaseApplication
import com.kecsot.basekecsot.BuildConfig
import com.kecsot.leitnerbox.common.CrashReportingTree
import com.kecsot.leitnerbox.dagger.components.*
import com.kecsot.leitnerbox.dagger.modules.AppModule
import timber.log.Timber

class
LeitnerBoxApplication : BaseApplication() {

    companion object {
        lateinit var instance: LeitnerBoxApplication
    }

    init {
        instance = this
    }

    public val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this)) // FIXME: deprication
            .build()
    }

    public val repositoryComponent: RepositoryComponent by lazy {
        DaggerRepositoryComponent
            .builder()
            .build()
    }

    public val localDataSourceComponent: LocalDataSourceComponent by lazy {
        DaggerLocalDataSourceComponent
            .builder()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        
        FirebaseApp.initializeApp(this)

        Timber.plant(
            if (BuildConfig.DEBUG)
                Timber.DebugTree()
            else
                CrashReportingTree()
        )
    }
}

