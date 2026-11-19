package com.judith.judith_franz


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

object CartasRepositorio {

    private const val ARCHIVO_PREFS = "coleccion_cartas"
    private const val CLAVE_CARTAS = "cartas_moon_edition_3"
    private const val CLAVE_ANTIGUA = "cartas"
    private const val ARCHIVO_CATALOGO = "cartas_moon_edition_3.json"
    const val EDICION = "Moon Edition Series 3"

    private var prefs: SharedPreferences? = null
    private val cartas = mutableListOf<Carta>()

    fun iniciar(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(ARCHIVO_PREFS, Context.MODE_PRIVATE)
        prefs = p
        p.edit { remove(CLAVE_ANTIGUA) }

        val guardada = p.getString(CLAVE_CARTAS, null)
            ?.let { json -> runCatching { desdeJson(json) }.getOrNull() }
        if (guardada != null) {
            cartas.addAll(guardada)
            return
        }

        val catalogo = context.assets.open(ARCHIVO_CATALOGO).bufferedReader().use { it.readText() }
        cartas.addAll(desdeCatalogo(catalogo))
        guardarEnMovil()
    }

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
        guardarEnMovil()
        return true
    }

    private fun guardarEnMovil() {
        prefs?.edit { putString(CLAVE_CARTAS, aJson(cartas)) }
    }


    private fun desdeCatalogo(json: String): List<Carta> {
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            Carta(
                codigo = o.getString("codigo"),
                nombre = o.getString("nombre"),
                rareza = o.getString("rareza"),
                edicion = EDICION,
                tengo = false,
                cantidad = 0,
                imagenUrl = o.optString("imagen"),
                horizontal = o.optBoolean("horizontal")
            )
        }
    }

    private fun aJson(lista: List<Carta>): String {
        val array = JSONArray()
        for (c in lista) {
            array.put(
                JSONObject()
                    .put("codigo", c.codigo)
                    .put("nombre", c.nombre)
                    .put("rareza", c.rareza)
                    .put("edicion", c.edicion)
                    .put("tengo", c.tengo)
                    .put("cantidad", c.cantidad)
                    .put("imagenUrl", c.imagenUrl)
                    .put("horizontal", c.horizontal)
            )
        }
        return array.toString()
    }

    private fun desdeJson(json: String): List<Carta> {
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            Carta(
                codigo = o.getString("codigo"),
                nombre = o.getString("nombre"),
                rareza = o.optString("rareza"),
                edicion = o.optString("edicion"),
                tengo = o.optBoolean("tengo"),
                cantidad = o.optInt("cantidad"),
                imagenUrl = o.optString("imagenUrl"),
                horizontal = o.optBoolean("horizontal")
            )
        }
    }
}
