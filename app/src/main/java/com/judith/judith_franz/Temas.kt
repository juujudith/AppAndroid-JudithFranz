package com.judith.judith_franz


import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.edit
import androidx.core.view.WindowCompat

object Temas {

    data class Tema(
        val id: String,
        @StringRes val nombre: Int,
        @StyleRes val estilo: Int,
        val oscuro: Boolean
    )

    val lista = listOf(
        Tema("luna", R.string.tema_luna, R.style.Theme_JudithFranz, false),
        Tema("applejack_light", R.string.tema_applejack_light, R.style.Theme_JudithFranz_Applejack_Light, false),
        Tema("applejack_dark", R.string.tema_applejack_dark, R.style.Theme_JudithFranz_Applejack_Dark, true),
        Tema("fluttershy_light", R.string.tema_fluttershy_light, R.style.Theme_JudithFranz_Fluttershy_Light, false),
        Tema("fluttershy_dark", R.string.tema_fluttershy_dark, R.style.Theme_JudithFranz_Fluttershy_Dark, true),
        Tema("pinkiepie_light", R.string.tema_pinkiepie_light, R.style.Theme_JudithFranz_PinkiePie_Light, false),
        Tema("pinkiepie_dark", R.string.tema_pinkiepie_dark, R.style.Theme_JudithFranz_PinkiePie_Dark, true),
        Tema("rainbowdash_light", R.string.tema_rainbowdash_light, R.style.Theme_JudithFranz_RainbowDash_Light, false),
        Tema("rainbowdash_dark", R.string.tema_rainbowdash_dark, R.style.Theme_JudithFranz_RainbowDash_Dark, true),
        Tema("rarity_light", R.string.tema_rarity_light, R.style.Theme_JudithFranz_Rarity_Light, false),
        Tema("rarity_dark", R.string.tema_rarity_dark, R.style.Theme_JudithFranz_Rarity_Dark, true),
        Tema("twilight_light", R.string.tema_twilight_light, R.style.Theme_JudithFranz_Twilight_Light, false),
        Tema("twilight_dark", R.string.tema_twilight_dark, R.style.Theme_JudithFranz_Twilight_Dark, true)
    )

    private const val ARCHIVO_PREFS = "ajustes"
    private const val CLAVE_TEMA = "tema"

    fun actual(context: Context): Tema {
        val id = context.getSharedPreferences(ARCHIVO_PREFS, Context.MODE_PRIVATE)
            .getString(CLAVE_TEMA, null)
        return lista.find { it.id == id } ?: lista.first()
    }

    fun guardar(context: Context, tema: Tema) {
        context.getSharedPreferences(ARCHIVO_PREFS, Context.MODE_PRIVATE)
            .edit { putString(CLAVE_TEMA, tema.id) }
    }

    fun iniciar(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            private val temaDePantalla = mutableMapOf<Activity, String>()

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val tema = actual(activity)
                activity.setTheme(tema.estilo)
                temaDePantalla[activity] = tema.id
            }

            override fun onActivityStarted(activity: Activity) {
                val claro = !actual(activity).oscuro
                WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
                    isAppearanceLightStatusBars = claro
                    isAppearanceLightNavigationBars = claro
                }
            }

            override fun onActivityResumed(activity: Activity) {
                if (temaDePantalla[activity] != actual(activity).id) activity.recreate()
            }

            override fun onActivityDestroyed(activity: Activity) {
                temaDePantalla.remove(activity)
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }
}
