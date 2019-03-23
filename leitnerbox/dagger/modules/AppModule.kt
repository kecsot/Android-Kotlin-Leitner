package com.kecsot.leitnerbox.dagger.modules

import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(val app: LeitnerBoxApplication) {

    @Provides
    @Singleton
    fun provideBaseContext() = app.baseContext

}