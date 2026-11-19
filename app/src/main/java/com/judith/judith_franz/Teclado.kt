package com.judith.judith_franz


import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams

private const val MARGEN_TECLADO_DP = 24

fun View.ajustarABarrasYTeclado() {
    val margen = (MARGEN_TECLADO_DP * resources.displayMetrics.density).toInt()

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())

        v.setPadding(barras.left, barras.top, barras.right, maxOf(0, barras.bottom - teclado.bottom))
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = teclado.bottom }

        if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
            v.doOnNextLayout { v.findFocus()?.subirConMargen(margen) }
        }
        insets
    }

    viewTreeObserver.addOnGlobalFocusChangeListener { _, campoNuevo ->
        val tecladoAbierto = ViewCompat.getRootWindowInsets(this)
            ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        if (campoNuevo != null && tecladoAbierto) campoNuevo.subirConMargen(margen)
    }
}

private fun View.subirConMargen(margen: Int) {
    post { requestRectangleOnScreen(Rect(0, 0, width, height + margen), false) }
}
