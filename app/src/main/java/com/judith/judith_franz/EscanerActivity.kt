package com.judith.judith_franz

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.graphics.Rect
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EscanerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "EscanerActivity"
    }

    private lateinit var tilCodigo: TextInputLayout
    private lateinit var etCodigo: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_escaner)
        configurarTeclado()

        BarraSuperior.configurar(this, conAtras = true)
        BarraInferior.configurar(this, R.id.nav_scan)

        tilCodigo = findViewById(R.id.tilCodigoEscaner)
        etCodigo = findViewById(R.id.etCodigoEscaner)

        etCodigo.doAfterTextChanged { tilCodigo.error = null }
        etCodigo.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_SEARCH) {
                buscar()
                true
            } else {
                false
            }
        }

        findViewById<Button>(R.id.btnPegarCodigo).setOnClickListener { pegar() }
        findViewById<Button>(R.id.btnBuscarCodigo).setOnClickListener { buscar() }
    }

    private fun configurarTeclado() {
        val raiz = findViewById<View>(R.id.main)
        val barraInferior = findViewById<View>(R.id.bottomBar)
        ViewCompat.setOnApplyWindowInsetsListener(raiz) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
            val abierto = insets.isVisible(WindowInsetsCompat.Type.ime())
            v.setPadding(
                barras.left,
                barras.top,
                barras.right,
                if (abierto) maxOf(barras.bottom, teclado.bottom) else barras.bottom
            )
            barraInferior.visibility = if (abierto) View.GONE else View.VISIBLE
            if (abierto) mostrarBotones()
            insets
        }
    }

    private fun mostrarBotones() {
        val fila = findViewById<View>(R.id.filaBotonesEscaner)
        fila.post {
            fila.requestRectangleOnScreen(Rect(0, 0, fila.width, fila.height), false)
        }
    }

    private fun pegar() {
        val portapapeles = getSystemService(ClipboardManager::class.java)
        val clip = portapapeles?.primaryClip
        val texto = if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).coerceToText(this)?.toString()?.trim().orEmpty()
        } else {
            ""
        }
        if (texto.isEmpty()) {
            tilCodigo.error = getString(R.string.portapapeles_vacio)
            Toast.makeText(this, R.string.portapapeles_vacio, Toast.LENGTH_SHORT).show()
            mostrarBotones()
            return
        }
        etCodigo.setText(texto)
        etCodigo.setSelection(texto.length)
    }

    private fun buscar() {
        val texto = etCodigo.text?.toString()?.trim().orEmpty()
        if (texto.isEmpty()) {
            tilCodigo.error = getString(R.string.error_campo_obligatorio)
            return
        }

        val carta = CartasRepositorio.buscarPorCodigo(texto)
        if (carta == null) {
            Log.d(TAG, "Código no encontrado: $texto")
            tilCodigo.error = getString(R.string.error_carta_no_existe, texto.uppercase())
            mostrarBotones()
            return
        }

        Log.d(TAG, "Carta encontrada: ${carta.codigo} (${carta.nombre})")
        tilCodigo.error = null
        if (PreferenciasApp.vibrar(this)) {
            etCodigo.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
        if (PreferenciasApp.guardarAutomaticamente(this) && !carta.tengo) {
            CartasRepositorio.modificar(carta.codigo, carta.conTengo(true))
            Log.d(TAG, "Carta marcada como conseguida desde el escáner: ${carta.codigo}")
            Toast.makeText(this, R.string.carta_anadida, Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(this, CardActivity::class.java)
            .putExtra(CardActivity.EXTRA_CODIGO, carta.codigo)
        startActivity(intent)
    }
}
