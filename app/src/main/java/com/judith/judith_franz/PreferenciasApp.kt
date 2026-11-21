package com.judith.judith_franz

import android.content.Context
import androidx.core.content.edit

// Guarda los ajustes
object PreferenciasApp {

    private const val ARCHIVO_PREFS = "ajustes"
    private const val CLAVE_GUARDAR_AUTO = "escaner_guardar_auto"
    private const val CLAVE_VIBRAR = "escaner_vibrar"

    fun guardarAutomaticamente(context: Context): Boolean =
        prefs(context).getBoolean(CLAVE_GUARDAR_AUTO, false)

    fun ponerGuardarAutomaticamente(context: Context, activado: Boolean) {
        prefs(context).edit { putBoolean(CLAVE_GUARDAR_AUTO, activado) }
    }

    // Mira si vibra
    fun vibrar(context: Context): Boolean =
        prefs(context).getBoolean(CLAVE_VIBRAR, true)

    fun ponerVibrar(context: Context, activado: Boolean) {
        prefs(context).edit { putBoolean(CLAVE_VIBRAR, activado) }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(ARCHIVO_PREFS, Context.MODE_PRIVATE)
}
