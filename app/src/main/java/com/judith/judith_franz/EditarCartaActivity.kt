package com.judith.judith_franz


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.util.Locale

class EditarCartaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODO_ANADIR = "EXTRA_MODO_ANADIR"
    }

    private var cartaOriginal: Carta? = null

    private lateinit var etNombre: EditText
    private lateinit var etCodigo: EditText
    private lateinit var actvRareza: MaterialAutoCompleteTextView
    private lateinit var etCantidad: EditText
    private lateinit var etEdicion: EditText
    private lateinit var swTengo: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_carta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombre = findViewById(R.id.etNombreCarta)
        etCodigo = findViewById(R.id.etCodigoCarta)
        actvRareza = findViewById(R.id.actvRarezaCarta)
        etCantidad = findViewById(R.id.etCantidadCarta)
        etEdicion = findViewById(R.id.etEdicionCarta)
        swTengo = findViewById(R.id.swTengoEditar)

        val modoAnadir = intent.getBooleanExtra(EXTRA_MODO_ANADIR, false)
        val carta = intent.getStringExtra(CardActivity.EXTRA_CODIGO)?.let { CartasRepositorio.buscar(it) }

        if (modoAnadir || carta == null) {
            findViewById<TextView>(R.id.tvEditarTitulo).setText(R.string.anadir_titulo)
            etNombre.setText("")
            etCodigo.setText("")
            etCantidad.setText("1")
            etEdicion.setText(CartasRepositorio.EDICION)
            swTengo.isChecked = true
        } else {
            cartaOriginal = carta
            findViewById<ImageView>(R.id.ivEditarCarta).cargarCarta(carta)
            etNombre.setText(carta.nombre)
            etCodigo.setText(carta.codigo)
            actvRareza.setText(carta.rareza, false)
            etCantidad.setText(String.format(Locale.US, "%d", carta.cantidad))
            etEdicion.setText(carta.edicion)
            swTengo.isChecked = carta.tengo
        }

        findViewById<ImageButton>(R.id.btnVolverEditar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancelarEditar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnGuardarCarta).setOnClickListener { guardar() }
    }

    private fun guardar() {
        val nombre = etNombre.text.toString().trim()
        val codigo = etCodigo.text.toString().trim().uppercase()

        if (nombre.isEmpty()) {
            etNombre.error = getString(R.string.error_campo_obligatorio)
            return
        }
        if (codigo.isEmpty()) {
            etCodigo.error = getString(R.string.error_campo_obligatorio)
            return
        }

        val cantidad = etCantidad.text.toString().toIntOrNull() ?: 0
        val base = Carta(
            codigo = codigo,
            nombre = nombre,
            rareza = actvRareza.text.toString(),
            edicion = etEdicion.text.toString().trim(),
            tengo = swTengo.isChecked,
            cantidad = cantidad,
            imagenUrl = cartaOriginal?.imagenUrl ?: "",
            horizontal = cartaOriginal?.horizontal ?: false
        )
        val carta = base.conTengo(swTengo.isChecked)

        if (!CartasRepositorio.guardar(cartaOriginal?.codigo, carta)) {
            etCodigo.error = getString(R.string.error_codigo_repetido)
            return
        }

        Toast.makeText(this, R.string.carta_guardada, Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK, Intent().putExtra(CardActivity.EXTRA_CODIGO, carta.codigo))
        finish()
    }
}
