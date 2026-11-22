package com.judith.judith_franz


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.material.navigation.NavigationBarView

object BarraInferior {

    fun configurar(activity: AppCompatActivity, seleccionado: Int) {
        val nav = activity.findViewById<NavigationBarView>(R.id.bottomNav)
        marcar(nav, seleccionado)

        nav.setOnItemSelectedListener { item ->
            val destino = when (item.itemId) {
                R.id.nav_home -> HomeActivity::class.java
                R.id.nav_scan -> EscanerActivity::class.java
                R.id.nav_settings -> SettingsActivity::class.java
                else -> return@setOnItemSelectedListener false
            }
            if (destino != activity.javaClass) {
                val intent = Intent(activity, destino)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                if (activity !is HomeActivity) activity.finish()
            }
            true
        }
        nav.setOnItemReselectedListener { }

        activity.lifecycle.addObserver(LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) marcar(nav, seleccionado)
        })
    }

    private fun marcar(nav: NavigationBarView, seleccionado: Int) {
        nav.menu.findItem(seleccionado)?.isChecked = true
    }
}
