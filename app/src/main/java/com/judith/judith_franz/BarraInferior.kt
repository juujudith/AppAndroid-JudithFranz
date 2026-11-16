package com.judith.judith_franz

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object BarraInferior {

    fun configurar(activity: AppCompatActivity, seleccionado: Int) {
        val nav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = seleccionado

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> if (activity !is HomeActivity) {
                    val intent = Intent(activity, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    activity.startActivity(intent)
                }
                R.id.nav_settings -> if (activity !is SettingsActivity) {
                    val intent = Intent(activity, SettingsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    activity.startActivity(intent)
                }
                R.id.nav_scan -> Toast.makeText(
                    activity, R.string.pronto_escaner, Toast.LENGTH_SHORT
                ).show()
            }
            false
        }
    }
}
