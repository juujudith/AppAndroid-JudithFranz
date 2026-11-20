package com.judith.judith_franz

import org.junit.Assert.assertEquals
import org.junit.Test

class FiltroCartasTest {

    private val cartas = listOf(
        Carta("MLPME03-R-001", "Princess Luna", "R", "Moon Edition Series 3", true, 2),
        Carta("MLPME03-SR-002", "Twilight Sparkle", "SR", "Moon Edition Series 3", false, 0),
        Carta("MLPME03-R-003", "Rarity", "R", "Moon Edition Series 3", false, 0)
    )

    @Test
    fun sinFiltros_devuelveTodas() {
        val resultado = FiltroCartas.filtrar(cartas, "", null, FiltroCartas.POSESION_TODAS)
        assertEquals(3, resultado.size)
    }

    @Test
    fun buscaPorNombreSinImportarMayusculas() {
        val resultado = FiltroCartas.filtrar(cartas, "luna", null, FiltroCartas.POSESION_TODAS)
        assertEquals(listOf("MLPME03-R-001"), resultado.map { it.codigo })
    }

    @Test
    fun buscaPorParteDelCodigo() {
        val resultado = FiltroCartas.filtrar(cartas, "sr-002", null, FiltroCartas.POSESION_TODAS)
        assertEquals(listOf("Twilight Sparkle"), resultado.map { it.nombre })
    }

    @Test
    fun filtraPorRareza() {
        val resultado = FiltroCartas.filtrar(cartas, "", "R", FiltroCartas.POSESION_TODAS)
        assertEquals(2, resultado.size)
    }

    @Test
    fun filtraLasQueTengoYLasQueFaltan() {
        val tengo = FiltroCartas.filtrar(cartas, "", null, FiltroCartas.POSESION_TENGO)
        val faltan = FiltroCartas.filtrar(cartas, "", null, FiltroCartas.POSESION_FALTAN)
        assertEquals(1, tengo.size)
        assertEquals(2, faltan.size)
    }

    @Test
    fun combinaFiltrosYPuedeQuedarVacio() {
        val resultado = FiltroCartas.filtrar(cartas, "Rarity", "SR", FiltroCartas.POSESION_TODAS)
        assertEquals(0, resultado.size)
    }
}
