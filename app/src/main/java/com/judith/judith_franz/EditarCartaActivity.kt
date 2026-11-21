package com.judith.judith_franz


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

// Editar carta
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
        findViewById<View>(R.id.main).ajustarABarrasYTeclado(
            mostrarConTeclado = findViewById(R.id.btnGuardarCarta)
        )

        val barra = BarraSuperior.configurar(this, conAtras = true)

        etNombre = findViewById(R.id.etNombreCarta)
        etCodigo = findViewById(R.id.etCodigoCarta)
        actvRareza = findViewById(R.id.actvRarezaCarta)
        etCantidad = findViewById(R.id.etCantidadCarta)
        etEdicion = findViewById(R.id.etEdicionCarta)
        swTengo = findViewById(R.id.swTengoEditar)

        val modoAnadir = intent.getBooleanExtra(EXTRA_MODO_ANADIR, false)
        val carta = intent.getStringExtra(CardActivity.EXTRA_CODIGO)?.let { CartasRepositorio.buscar(it) }

        cartaOriginal = if (modoAnadir) null else carta
        val original = cartaOriginal
        if (original == null) {
            barra.setTitle(R.string.anadir_titulo)
        } else {
            findViewById<ImageView>(R.id.ivEditarCarta).cargarCarta(original)
        }

        if (savedInstanceState == null) {
            if (original == null) {
                etNombre.setText("")
                etCodigo.setText("")
                etCantidad.setText(String.format(Locale.US, "%d", 1))
                etEdicion.setText(CartasRepositorio.EDICION)
                actvRareza.setText(resources.getStringArray(R.array.rarezas_carta).first(), false)
                swTengo.isChecked = true
            } else {
                etNombre.setText(original.nombre)
                etCodigo.setText(original.codigo)
                actvRareza.setText(original.rareza, false)
                etCantidad.setText(String.format(Locale.US, "%d", original.cantidad))
                etEdicion.setText(original.edicion)
                swTengo.isChecked = original.tengo
            }
        }

        listOf(R.id.tilNombreCarta, R.id.tilCodigoCarta, R.id.tilRarezaCarta, R.id.tilCantidadCarta)
            .forEach { findViewById<TextInputLayout>(it).limpiarErrorAlEscribir() }

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

    // Guarda la carta
    private fun guardar() {
        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombreCarta)
        val tilCodigo = findViewById<TextInputLayout>(R.id.tilCodigoCarta)
        val tilRareza = findViewById<TextInputLayout>(R.id.tilRarezaCarta)
        val tilCantidad = findViewById<TextInputLayout>(R.id.tilCantidadCarta)

        val nombre = etNombre.text.toString().trim()
        val codigo = etCodigo.text.toString().trim().uppercase()
        val rareza = actvRareza.text.toString().trim()
        val cantidad = etCantidad.text.toString().toIntOrNull()

        tilNombre.error = if (nombre.isEmpty()) getString(R.string.error_campo_obligatorio) else null
        tilCodigo.error = if (codigo.isEmpty()) getString(R.string.error_campo_obligatorio) else null
        tilRareza.error = if (rareza.isEmpty()) getString(R.string.error_campo_obligatorio) else null
        tilCantidad.error = if (cantidad == null || cantidad < 0) {
            getString(R.string.error_cantidad_invalida)
        } else {
            null
        }

        val primerError = listOf(tilNombre, tilCodigo, tilRareza, tilCantidad).firstOrNull { it.error != null }
        if (primerError != null || cantidad == null) {
            primerError?.mostrarEnPantalla()
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
                tilCodigo.error = getString(R.string.error_codigo_repetido)
                tilCodigo.mostrarEnPantalla()
                return
            }
            Log.d(TAG, "Carta añadida: ${carta.codigo} (${carta.nombre})")
            Toast.makeText(this, R.string.carta_anadida, Toast.LENGTH_SHORT).show()
        } else {
            if (!CartasRepositorio.modificar(original.codigo, carta)) {
                tilCodigo.error = getString(R.string.error_codigo_repetido)
                tilCodigo.mostrarEnPantalla()
                return
            }
            Log.d(TAG, "Carta modificada: ${original.codigo} -> ${carta.codigo} (${carta.nombre})")
            Toast.makeText(this, R.string.carta_modificada, Toast.LENGTH_SHORT).show()
        }
        setResult(Activity.RESULT_OK, Intent().putExtra(CardActivity.EXTRA_CODIGO, carta.codigo))
        finish()
    }
}
