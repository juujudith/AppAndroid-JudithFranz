package com.judith.judith_franz


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

// App al arrancar
class JudithFranzApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Temas.iniciar(this)
        CartasRepositorio.iniciar(this)
    }
}
