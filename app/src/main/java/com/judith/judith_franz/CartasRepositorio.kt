package com.judith.judith_franz

object CartasRepositorio {

    private const val EDICION = "Moon Edition Series 3"
    private val cartas = mutableListOf(
        Carta("MLPME03-SC-001L5", "Princess Twilight Sparkle", "SC TIER", EDICION, tengo = true, cantidad = 1),
        Carta("MLPME03-SSR-002", "Princess Twilight Sparkle", "SSR", EDICION, tengo = false, cantidad = 0),
        Carta("MLPME03-SR-003", "Princess Twilight Sparkle", "SR", EDICION, tengo = true, cantidad = 1),
        Carta("MLPME03-R-004", "Princess Twilight Sparkle", "R", EDICION, tengo = true, cantidad = 2)
    )

    fun todas(): List<Carta> = cartas.toList()
    fun buscar(codigo: String): Carta? = cartas.find { it.codigo == codigo }
    fun guardar(codigoOriginal: String?, carta: Carta): Boolean {
        val usadoPorOtra = cartas.any { it.codigo == carta.codigo && it.codigo != codigoOriginal }
        if (usadoPorOtra) return false

        val posicion = cartas.indexOfFirst { it.codigo == codigoOriginal }
        if (codigoOriginal == null || posicion == -1) {
            cartas.add(carta)
        } else {
            cartas[posicion] = carta
        }
        return true
    }
}
