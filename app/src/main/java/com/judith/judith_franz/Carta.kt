package com.judith.judith_franz


// Datos de una carta
data class Carta(
    val codigo: String,
    val nombre: String,
    val rareza: String,
    val edicion: String,
    val tengo: Boolean,
    val cantidad: Int,
    val imagenUrl: String = "",
    val horizontal: Boolean = false
) {
    // Cambia si la tengo
    fun conTengo(nuevoTengo: Boolean): Carta =
        copy(tengo = nuevoTengo, cantidad = if (nuevoTengo) maxOf(1, cantidad) else 0)

    fun conCantidad(nuevaCantidad: Int): Carta {
        val n = maxOf(0, nuevaCantidad)
        return copy(cantidad = n, tengo = n > 0)
    }
}
