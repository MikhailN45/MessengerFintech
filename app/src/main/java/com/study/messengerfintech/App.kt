package com.study.messengerfintech

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.study.messengerfintech.di.AppComponent
import com.study.messengerfintech.di.DaggerAppComponent

class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        appComponent = DaggerAppComponent.factory().create(this)
        INSTANCE = this
    }

    companion object {
        lateinit var appContext: Context
        lateinit var INSTANCE: App
    }
}

fun Activity.getComponent(): AppComponent = (application as App).appComponent
fun Fragment.getComponent(): AppComponent = (requireActivity().application as App).appComponent