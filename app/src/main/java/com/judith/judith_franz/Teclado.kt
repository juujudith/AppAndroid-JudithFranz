package com.judith.judith_franz


import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import com.google.android.material.textfield.TextInputLayout

private const val MARGEN_TECLADO_DP = 24

fun View.ajustarABarrasYTeclado(
    ocultarConTeclado: View? = null,
    mostrarConTeclado: View? = null
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
        val abierto = insets.isVisible(WindowInsetsCompat.Type.ime())

        val abajo = if (abierto) maxOf(barras.bottom, teclado.bottom) else barras.bottom
        v.setPadding(barras.left, barras.top, barras.right, abajo)
        ocultarConTeclado?.visibility = if (abierto) View.GONE else View.VISIBLE

        if (abierto) {
            v.doOnNextLayout { mostrarJuntos(v.findFocus(), mostrarConTeclado) }
        }
        insets
    }

    ViewCompat.setWindowInsetsAnimationCallback(
        this,
        object : WindowInsetsAnimationCompat.Callback(
            WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
        ) {
            override fun onProgress(
                insets: WindowInsetsCompat,
                animaciones: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat = insets

            override fun onEnd(animacion: WindowInsetsAnimationCompat) {
                val abierto = ViewCompat.getRootWindowInsets(this@ajustarABarrasYTeclado)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true
                val esTeclado = (animacion.typeMask and WindowInsetsCompat.Type.ime()) != 0
                if (esTeclado && abierto) {
                    mostrarJuntos(findFocus(), mostrarConTeclado)
                }
            }
        }
    )

    viewTreeObserver.addOnGlobalFocusChangeListener { _, campoNuevo ->
        val tecladoAbierto = ViewCompat.getRootWindowInsets(this)
            ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        if (campoNuevo != null && tecladoAbierto) {
            mostrarJuntos(campoNuevo, mostrarConTeclado)
        }
    }
}

fun View.mostrarEnPantalla() {
    mostrarJuntos(this, null)
}

fun mostrarJuntos(campo: View?, boton: View?) {
    val objetivo = campo?.let { it.contenedorDeCampo() ?: it }
    val referencia = objetivo ?: boton ?: return
    referencia.doOnPreDraw {
        val margen = (MARGEN_TECLADO_DP * referencia.resources.displayMetrics.density).toInt()
        val scroll = referencia.scrollPadre()
        if (!boton.estaDentroDeAlgo(scroll)) boton?.pedirRectangulo(margen)
        if (scroll == null) {
            objetivo?.pedirRectangulo(margen)
            return@doOnPreDraw
        }

        val visibleArriba = scroll.paddingTop
        val visibleAbajo = scroll.height - scroll.paddingBottom
        val alto = visibleAbajo - visibleArriba
        if (alto <= 0) return@doOnPreDraw

        val zonaCampo = objetivo?.takeIf { it !== scroll }?.let { scroll.rectanguloDe(it, margen) }
        val zonaBoton = boton?.takeIf { it.isShown && it.estaDentroDe(scroll) }?.let { scroll.rectanguloDe(it, margen) }
        val zona = Rect(zonaCampo ?: zonaBoton ?: return@doOnPreDraw)
        if (zonaBoton != null) zona.union(zonaBoton)
        if (zona.height() > alto && zonaCampo != null) zona.set(zonaCampo)

        var destino = scroll.scrollY
        if (zona.bottom > destino + visibleAbajo) destino = zona.bottom - visibleAbajo
        if (zona.top < destino + visibleArriba) destino = zona.top - visibleArriba
        scroll.smoothScrollTo(0, maxOf(0, destino))
    }
    referencia.invalidate()
}

private fun View.estaDentroDe(grupo: ViewGroup): Boolean {
    var actual = parent
    while (actual is View) {
        if (actual === grupo) return true
        actual = actual.parent
    }
    return false
}

private fun View?.estaDentroDeAlgo(grupo: ViewGroup?): Boolean =
    this != null && grupo != null && estaDentroDe(grupo)

private fun View.pedirRectangulo(margen: Int) {
    requestRectangleOnScreen(Rect(0, 0, width, height + margen), false)
}

private fun View.contenedorDeCampo(): View? {
    var actual = parent
    while (actual is View) {
        if (actual is TextInputLayout) return actual
        actual = actual.parent
    }
    return null
}

private fun View.scrollPadre(): ScrollView? {
    if (this is ScrollView) return this
    var actual = parent
    while (actual is View) {
        if (actual is ScrollView) return actual
        actual = actual.parent
    }
    return null
}

private fun ViewGroup.rectanguloDe(vista: View, margen: Int): Rect {
    val rect = Rect(0, -margen, vista.width, vista.height + margen)
    offsetDescendantRectToMyCoords(vista, rect)
    return rect
}
