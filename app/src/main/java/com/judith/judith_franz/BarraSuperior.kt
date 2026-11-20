package com.judith.judith_franz

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.MaterialColors

// Barra superior
object BarraSuperior {

    fun configurar(
        activity: AppCompatActivity,
        conAtras: Boolean,
        alPulsarAccion: (Int) -> Boolean = { false }
    ): MaterialToolbar {
        val barra = activity.findViewById<MaterialToolbar>(R.id.barraSuperior)
        val color = MaterialColors.getColor(barra, R.attr.temaTexto)

        if (conAtras) {  // Flecha
            barra.setNavigationIconTint(color)
            barra.setNavigationContentDescription(R.string.desc_volver)
            barra.setNavigationOnClickListener { activity.finish() }
        } else {
            barra.navigationIcon = null
        }

        barra.overflowIcon?.mutate()?.setTint(color)
        for (i in 0 until barra.menu.size()) {
            barra.menu.getItem(i).icon?.mutate()?.setTint(color)
        }
        // Opciones del menu
        barra.setOnMenuItemClickListener { item -> alPulsarAccion(item.itemId) }
        return barra
    }
}
