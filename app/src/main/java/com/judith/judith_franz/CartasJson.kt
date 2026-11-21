package com.judith.judith_franz

import org.json.JSONArray
import org.json.JSONObject

// Pasa cartas a json
object CartasJson {

    fun desdeCatalogo(json: String, edicion: String): List<Carta> {
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            Carta(
                codigo = o.getString("codigo"),
                nombre = o.getString("nombre"),
                rareza = o.getString("rareza"),
                edicion = edicion,
                tengo = false,
                cantidad = 0,
                imagenUrl = o.optString("imagen"),
                horizontal = o.optBoolean("horizontal")
            )
        }
    }

    // Cartas a texto
    fun aJson(lista: List<Carta>): String {
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

    fun desdeJson(json: String): List<Carta> {
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
