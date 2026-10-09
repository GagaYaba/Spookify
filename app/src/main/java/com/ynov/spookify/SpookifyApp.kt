package com.ynov.spookify

import android.app.Application
import com.ynov.spookify.di.AppContainer

class SpookifyApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
