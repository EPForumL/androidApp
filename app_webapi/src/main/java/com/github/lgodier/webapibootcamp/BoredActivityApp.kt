package com.github.lgodier.webapibootcamp

import android.app.Application

open class BoredActivityApp : Application() {
    open fun getBaseUrl() = "https://www.boredapi.com/api/"
}