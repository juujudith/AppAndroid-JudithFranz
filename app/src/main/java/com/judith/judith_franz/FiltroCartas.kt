package com.judith.judith_franz

// Filtra las cartas
object FiltroCartas {

    const val POSESION_TODAS = 0
    const val POSESION_TENGO = 1
    const val POSESION_FALTAN = 2

    // Busca y filtra
    fun filtrar(
        cartas: List<Carta>,
        texto: String,
        rareza: String?,
        posesion: Int
    ): List<Carta> {
        val buscado = texto.trim()
        return cartas.filter { carta ->
            val coincideTexto = buscado.isEmpty() ||
                carta.codigo.contains(buscado, ignoreCase = true) ||
                carta.nombre.contains(buscado, ignoreCase = true)
            val coincideRareza = rareza.isNullOrBlank() || carta.rareza.equals(rareza, ignoreCase = true)
            val coincidePosesion = when (posesion) {
                POSESION_TENGO -> carta.tengo
                POSESION_FALTAN -> !carta.tengo
                else -> true
            }
            coincideTexto && coincideRareza && coincidePosesion
        }
    }
}
