package com.judith.judith_franz


import android.widget.ImageView
import coil.load

fun ImageView.cargarCarta(carta: Carta) {
    scaleType = if (carta.horizontal) ImageView.ScaleType.FIT_CENTER else ImageView.ScaleType.CENTER_CROP

    if (carta.imagenUrl.isBlank()) {
        setImageResource(R.drawable.carta_placeholder)
        return
    }
    load(carta.imagenUrl) {
        crossfade(true)
        placeholder(R.drawable.carta_placeholder)
        error(R.drawable.carta_placeholder)
    }
}
