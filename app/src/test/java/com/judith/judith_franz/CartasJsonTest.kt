package com.judith.judith_franz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartasJsonTest {

    @Test
    fun guardarYLeer_devuelveLasMismasCartas() {
        val cartas = listOf(
            Carta("MLPME03-R-001", "Princess Luna", "R", "Moon Edition Series 3", true, 2, "https://ejemplo/luna.png", false),
            Carta("MLPME03-SR-002", "Twilight Sparkle", "SR", "Moon Edition Series 3", false, 0, "", true)
        )
        val leidas = CartasJson.desdeJson(CartasJson.aJson(cartas))
        assertEquals(cartas, leidas)
    }

    @Test
    fun catalogo_empiezaSinTenerNinguna() {
        val json = """[{"codigo":"MLPME03-R-001","nombre":"Princess Luna","rareza":"R","imagen":"https://ejemplo/luna.png"}]"""
        val cartas = CartasJson.desdeCatalogo(json, "Moon Edition Series 3")
        assertEquals(1, cartas.size)
        val carta = cartas.first()
        assertFalse(carta.tengo)
        assertEquals(0, carta.cantidad)
        assertEquals("Moon Edition Series 3", carta.edicion)
        assertEquals("https://ejemplo/luna.png", carta.imagenUrl)
        assertFalse(carta.horizontal)
    }

    @Test
    fun leerJsonViejoSinCamposOpcionales_ponValoresPorDefecto() {
        val json = """[{"codigo":"MLPME03-R-001","nombre":"Princess Luna"}]"""
        val carta = CartasJson.desdeJson(json).first()
        assertEquals("", carta.rareza)
        assertEquals(0, carta.cantidad)
        assertFalse(carta.tengo)
    }

    @Test
    fun listaVacia_seGuardaComoArrayVacio() {
        assertTrue(CartasJson.desdeJson(CartasJson.aJson(emptyList())).isEmpty())
    }
}
