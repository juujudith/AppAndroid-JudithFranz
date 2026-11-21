package com.judith.judith_franz


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// Cartas de la coleccion
object CartasRepositorio {

    private const val ARCHIVO_PREFS = "coleccion_cartas"
    private const val CLAVE_CARTAS = "cartas_moon_edition_3"
    private const val CLAVE_ANTIGUA = "cartas"
    private const val ARCHIVO_CATALOGO = "cartas_moon_edition_3.json"
    const val EDICION = "Moon Edition Series 3"

    private var prefs: SharedPreferences? = null
    private val cartas = mutableListOf<Carta>()

    // Cargar cartas
    fun iniciar(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(ARCHIVO_PREFS, Context.MODE_PRIVATE)
        prefs = p
        p.edit { remove(CLAVE_ANTIGUA) }

        val guardada = p.getString(CLAVE_CARTAS, null)
            ?.let { json -> runCatching { CartasJson.desdeJson(json) }.getOrNull() }
        if (guardada != null) {
            cartas.addAll(guardada)
            return
        }

        val catalogo = context.assets.open(ARCHIVO_CATALOGO).bufferedReader().use { it.readText() }
        cartas.addAll(CartasJson.desdeCatalogo(catalogo, EDICION))
        guardarEnMovil()
    }

    fun todas(): List<Carta> = cartas.toList()

    fun buscar(codigo: String): Carta? = cartas.find { it.codigo == codigo }

    fun buscarPorCodigo(texto: String): Carta? {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return null
        cartas.find { it.codigo.equals(limpio, ignoreCase = true) }?.let { return it }
        val parecidas = cartas.filter { it.codigo.contains(limpio, ignoreCase = true) }
        return parecidas.singleOrNull()
    }

    fun repetidas(): List<Carta> = cartas.filter { it.cantidad > 1 }

    fun existe(codigo: String): Boolean = cartas.any { it.codigo == codigo }

    // Anadir carta
    fun anadir(carta: Carta): Boolean {
        if (existe(carta.codigo)) return false
        cartas.add(carta)
        guardarEnMovil()
        return true
    }

    fun modificar(codigoOriginal: String, carta: Carta): Boolean {
        val posicion = cartas.indexOfFirst { it.codigo == codigoOriginal }
        if (posicion == -1) return false
        val usadoPorOtra = cartas.any { it.codigo == carta.codigo && it.codigo != codigoOriginal }
        if (usadoPorOtra) return false
        cartas[posicion] = carta
        guardarEnMovil()
        return true
    }

    fun eliminar(codigo: String): Int {
        val posicion = cartas.indexOfFirst { it.codigo == codigo }
        if (posicion == -1) return -1
        cartas.removeAt(posicion)
        guardarEnMovil()
        return posicion
    }

    fun restaurar(carta: Carta, posicion: Int) {
        if (existe(carta.codigo)) return
        cartas.add(posicion.coerceIn(0, cartas.size), carta)
        guardarEnMovil()
    }

    private fun guardarEnMovil() {
        prefs?.edit { putString(CLAVE_CARTAS, CartasJson.aJson(cartas)) }
    }
}
