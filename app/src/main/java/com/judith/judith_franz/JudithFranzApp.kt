package com.judith.judith_franz


import android.app.Application

class JudithFranzApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CartasRepositorio.iniciar(this)
    }
}
