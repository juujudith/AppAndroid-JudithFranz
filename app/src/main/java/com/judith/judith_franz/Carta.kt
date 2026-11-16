package com.judith.judith_franz

import androidx.annotation.DrawableRes

data class Carta(
    val codigo: String,
    val nombre: String,
    val rareza: String,
    val edicion: String,
    val tengo: Boolean,
    val cantidad: Int,
    @param:DrawableRes val imagen: Int = R.drawable.carta_twilight_sc001
) {
    fun conTengo(nuevoTengo: Boolean): Carta =
        copy(tengo = nuevoTengo, cantidad = if (nuevoTengo) maxOf(1, cantidad) else 0)

    fun conCantidad(nuevaCantidad: Int): Carta {
        val n = maxOf(0, nuevaCantidad)
        return copy(cantidad = n, tengo = n > 0)
    }
}
