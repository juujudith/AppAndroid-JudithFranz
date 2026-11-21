package com.judith.judith_franz


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView

object BarraInferior {

    fun configurar(activity: AppCompatActivity, seleccionado: Int) {
        val nav = activity.findViewById<NavigationBarView>(R.id.bottomNav)
        nav.selectedItemId = seleccionado

        nav.setOnItemSelectedListener { item ->
            val destino = when (item.itemId) {
                R.id.nav_home -> HomeActivity::class.java
                R.id.nav_scan -> EscanerActivity::class.java
                R.id.nav_settings -> SettingsActivity::class.java
                else -> null
            }
            if (destino != null && destino != activity.javaClass) {
                val intent = Intent(activity, destino)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                if (activity !is HomeActivity) activity.finish()
            }
            false
        }
    }
}
