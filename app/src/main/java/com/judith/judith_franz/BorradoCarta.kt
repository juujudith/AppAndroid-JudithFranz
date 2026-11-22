package com.judith.judith_franz

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

object BorradoCarta {

    private const val TAG = "BorradoCarta"

    private var pendiente: Pair<Carta, Int>? = null

    fun confirmar(
        activity: AppCompatActivity,
        carta: Carta,
        alConfirmar: () -> Unit,
        alCancelar: () -> Unit = {}
    ) {
        var confirmado = false
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.dialogo_eliminar_titulo)
            .setMessage(activity.getString(R.string.dialogo_eliminar_mensaje, carta.nombre, carta.codigo))
            .setNegativeButton(R.string.btn_cancelar, null)
            .setPositiveButton(R.string.btn_eliminar) { _, _ ->
                confirmado = true
                alConfirmar()
            }
            .setOnDismissListener {
                if (!confirmado) alCancelar()
            }
            .show()
    }

    fun dejarAviso(carta: Carta, posicion: Int) {
        pendiente = carta to posicion
    }

    fun mostrarAvisoPendiente(activity: AppCompatActivity, vista: View, alCambiar: () -> Unit) {
        val (carta, posicion) = pendiente ?: return
        pendiente = null
        mostrarAviso(activity, vista, carta, posicion, alCambiar)
    }

    fun mostrarAviso(
        activity: AppCompatActivity,
        vista: View,
        carta: Carta,
        posicion: Int,
        alCambiar: () -> Unit
    ) {
        val aviso = Snackbar.make(vista, R.string.carta_eliminada, Snackbar.LENGTH_LONG)
            .setAction(R.string.btn_deshacer) {
                CartasRepositorio.restaurar(carta, posicion)
                Log.d(TAG, "Carta restaurada: ${carta.codigo} (${carta.nombre})")
                alCambiar()
            }
        val barra = activity.findViewById<View>(R.id.bottomNav)
        if (barra is BottomNavigationView && barra.isShown) {
            aviso.anchorView = barra
        }
        aviso.show()
    }
}
