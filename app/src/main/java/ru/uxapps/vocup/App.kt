package ru.uxapps.vocup

import android.app.Application
import ru.uxapps.vocup.di.DaggerAppComponent

class App : Application() {
    val appComponent by lazy { DaggerAppComponent.factory().create(this) }
}