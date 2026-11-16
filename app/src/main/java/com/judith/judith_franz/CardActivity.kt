package com.judith.judith_franz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class CardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CODIGO = "EXTRA_CODIGO"
    }

    private lateinit var codigo: String

    private lateinit var ivCarta: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvRareza: TextView
    private lateinit var tvCodigo: TextView
    private lateinit var tvEdicion: TextView
    private lateinit var tvEstado: TextView
    private lateinit var switchTengo: SwitchCompat
    private lateinit var tvCantidad: TextView

    private val editarCarta = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            resultado.data?.getStringExtra(EXTRA_CODIGO)?.let { codigo = it }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val codigoRecibido = intent.getStringExtra(EXTRA_CODIGO)
        if (codigoRecibido == null) {
            finish()
            return
        }
        codigo = codigoRecibido

        BarraInferior.configurar(this, R.id.nav_home)

        ivCarta = findViewById(R.id.ivDetalleCarta)
        tvNombre = findViewById(R.id.tvDetalleNombre)
        tvRareza = findViewById(R.id.tvDetalleRareza)
        tvCodigo = findViewById(R.id.tvDetalleCodigo)
        tvEdicion = findViewById(R.id.tvDetalleEdicion)
        tvEstado = findViewById(R.id.tvEstadoPosesion)
        switchTengo = findViewById(R.id.switchTengoCarta)
        tvCantidad = findViewById(R.id.tvCantidadCarta)

        findViewById<ImageButton>(R.id.btnVolverHome).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.btnSumarRepetida).setOnClickListener {
            actualizar { it.conCantidad(it.cantidad + 1) }
        }
        findViewById<ImageButton>(R.id.btnRestarRepetida).setOnClickListener {
            actualizar { it.conCantidad(it.cantidad - 1) }
        }

        findViewById<Button>(R.id.btnModificarCarta).setOnClickListener {
            val intent = Intent(this, EditarCartaActivity::class.java)
                .putExtra(EXTRA_CODIGO, codigo)
            editarCarta.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mostrarCarta()
    }

    private fun actualizar(cambio: (Carta) -> Carta) {
        val carta = CartasRepositorio.buscar(codigo) ?: return
        CartasRepositorio.guardar(codigo, cambio(carta))
        mostrarCarta()
    }

    private fun mostrarCarta() {
        val carta = CartasRepositorio.buscar(codigo)
        if (carta == null) {
            finish()
            return
        }

        ivCarta.setImageResource(carta.imagen)
        ivCarta.contentDescription = getString(R.string.desc_imagen_carta, carta.nombre, carta.codigo)
        ivCarta.alpha = if (carta.tengo) 1f else 0.55f
        tvNombre.text = carta.nombre
        tvRareza.text = getString(R.string.etiqueta_rareza, carta.rareza)
        tvRareza.contentDescription = tvRareza.text
        tvCodigo.text = carta.codigo
        tvEdicion.text = carta.edicion
        tvCantidad.text = String.format(Locale.getDefault(), "%d", carta.cantidad)

        if (carta.tengo) {
            tvEstado.setText(R.string.estado_en_coleccion)
            tvEstado.setTextColor(ContextCompat.getColor(this, R.color.verde_ok))
        } else {
            tvEstado.setText(R.string.estado_falta)
            tvEstado.setTextColor(ContextCompat.getColor(this, R.color.rojo_error))
        }

        switchTengo.setOnCheckedChangeListener(null)
        switchTengo.isChecked = carta.tengo
        switchTengo.setOnCheckedChangeListener { _, marcado ->
            actualizar { it.conTengo(marcado) }
        }
    }
}
