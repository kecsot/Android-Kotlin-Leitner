package com.kecsot.leitnerbox.dagger.components

import com.kecsot.leitnerbox.dagger.modules.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

}