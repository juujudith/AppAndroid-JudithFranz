package com.judith.judith_franz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartaTest {

    private val base = Carta(
        codigo = "MLPME03-R-001",
        nombre = "Princess Luna",
        rareza = "R",
        edicion = "Moon Edition Series 3",
        tengo = false,
        cantidad = 0
    )

    @Test
    fun conTengo_true_ponePorLoMenosUnaCopia() {
        val carta = base.conTengo(true)
        assertTrue(carta.tengo)
        assertEquals(1, carta.cantidad)
    }

    @Test
    fun conTengo_true_mantieneLaCantidadSiYaHabia() {
        val carta = base.copy(tengo = true, cantidad = 3).conTengo(true)
        assertEquals(3, carta.cantidad)
    }

    @Test
    fun conTengo_false_dejaLaCantidadACero() {
        val carta = base.copy(tengo = true, cantidad = 2).conTengo(false)
        assertFalse(carta.tengo)
        assertEquals(0, carta.cantidad)
    }

    @Test
    fun conCantidad_positiva_marcaQueLaTengo() {
        val carta = base.conCantidad(4)
        assertTrue(carta.tengo)
        assertEquals(4, carta.cantidad)
    }

    @Test
    fun conCantidad_negativa_seQuedaEnCero() {
        val carta = base.copy(tengo = true, cantidad = 1).conCantidad(-5)
        assertFalse(carta.tengo)
        assertEquals(0, carta.cantidad)
    }

    @Test
    fun copy_noCambiaLaCartaOriginal() {
        val copia = base.copy(nombre = "Otra")
        assertEquals("Princess Luna", base.nombre)
        assertEquals("Otra", copia.nombre)
        assertEquals(base.codigo, copia.codigo)
    }
}
