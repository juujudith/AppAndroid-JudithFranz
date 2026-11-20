package com.judith.judith_franz


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class EditarCartaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODO_ANADIR = "EXTRA_MODO_ANADIR"
        private const val TAG = "EditarCartaActivity"
    }

    private var cartaOriginal: Carta? = null
    private var sincronizando = false

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

        val barra = BarraSuperior.configurar(this, conAtras = true)

        etNombre = findViewById(R.id.etNombreCarta)
        etCodigo = findViewById(R.id.etCodigoCarta)
        actvRareza = findViewById(R.id.actvRarezaCarta)
        etCantidad = findViewById(R.id.etCantidadCarta)
        etEdicion = findViewById(R.id.etEdicionCarta)
        swTengo = findViewById(R.id.swTengoEditar)

        val modoAnadir = intent.getBooleanExtra(EXTRA_MODO_ANADIR, false)
        val carta = intent.getStringExtra(CardActivity.EXTRA_CODIGO)?.let { CartasRepositorio.buscar(it) }

        if (modoAnadir || carta == null) {
            barra.setTitle(R.string.anadir_titulo)
            etNombre.setText("")
            etCodigo.setText("")
            etCantidad.setText(String.format(Locale.US, "%d", 1))
            etEdicion.setText(CartasRepositorio.EDICION)
            actvRareza.setText(resources.getStringArray(R.array.rarezas_carta).first(), false)
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

        sincronizarCantidadYTengo()

        findViewById<Button>(R.id.btnCancelarEditar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnGuardarCarta).setOnClickListener { guardar() }
    }

    private fun sincronizarCantidadYTengo() {
        etCantidad.doAfterTextChanged { texto ->
            val numero = texto?.toString()?.toIntOrNull() ?: return@doAfterTextChanged
            if (sincronizando) return@doAfterTextChanged
            sincronizando = true
            swTengo.isChecked = numero > 0
            sincronizando = false
        }
        swTengo.setOnCheckedChangeListener { _, marcado ->
            if (sincronizando) return@setOnCheckedChangeListener
            sincronizando = true
            val numero = etCantidad.text.toString().toIntOrNull() ?: 0
            if (marcado && numero == 0) {
                etCantidad.setText(String.format(Locale.US, "%d", 1))
            } else if (!marcado && numero > 0) {
                etCantidad.setText(String.format(Locale.US, "%d", 0))
            }
            sincronizando = false
        }
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

        val tilRareza = findViewById<TextInputLayout>(R.id.tilRarezaCarta)
        val rareza = actvRareza.text.toString().trim()
        if (rareza.isEmpty()) {
            tilRareza.error = getString(R.string.error_campo_obligatorio)
            return
        }
        tilRareza.error = null

        val cantidad = etCantidad.text.toString().toIntOrNull()
        if (cantidad == null || cantidad < 0) {
            etCantidad.error = getString(R.string.error_cantidad_invalida)
            return
        }

        val base = Carta(
            codigo = codigo,
            nombre = nombre,
            rareza = rareza,
            edicion = etEdicion.text.toString().trim(),
            tengo = swTengo.isChecked,
            cantidad = cantidad,
            imagenUrl = cartaOriginal?.imagenUrl ?: "",
            horizontal = cartaOriginal?.horizontal ?: false
        )
        val carta = base.conCantidad(if (swTengo.isChecked) maxOf(1, cantidad) else cantidad)

        val original = cartaOriginal
        if (original == null) {
            if (!CartasRepositorio.anadir(carta)) {
                etCodigo.error = getString(R.string.error_codigo_repetido)
                return
            }
            Log.d(TAG, "Carta añadida: ${carta.codigo} (${carta.nombre})")
            Toast.makeText(this, R.string.carta_anadida, Toast.LENGTH_SHORT).show()
        } else {
            if (!CartasRepositorio.modificar(original.codigo, carta)) {
                etCodigo.error = getString(R.string.error_codigo_repetido)
                return
            }
            Log.d(TAG, "Carta modificada: ${original.codigo} -> ${carta.codigo} (${carta.nombre})")
            Toast.makeText(this, R.string.carta_modificada, Toast.LENGTH_SHORT).show()
        }
        setResult(Activity.RESULT_OK, Intent().putExtra(CardActivity.EXTRA_CODIGO, carta.codigo))
        finish()
    }
}
