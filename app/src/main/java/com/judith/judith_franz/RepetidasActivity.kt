package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

// Lista de repetidas
class RepetidasActivity : AppCompatActivity() {

    private lateinit var rvRepetidas: RecyclerView
    private lateinit var tvVacio: TextView

    private val adapter = RepetidaAdapter { carta ->
        val intent = Intent(this, CardActivity::class.java)
            .putExtra(CardActivity.EXTRA_CODIGO, carta.codigo)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repetidas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BarraSuperior.configurar(this, conAtras = true)

        rvRepetidas = findViewById(R.id.rvRepetidas)
        tvVacio = findViewById(R.id.tvRepetidasVacio)
        rvRepetidas.adapter = adapter
    }

    // Recarga la lista
    override fun onResume() {
        super.onResume()
        refrescar()
        BorradoCarta.mostrarAvisoPendiente(this, findViewById(R.id.main)) { refrescar() }
    }

    private fun refrescar() {
        val repetidas = CartasRepositorio.repetidas()
        adapter.submitList(repetidas)
        val vacia = repetidas.isEmpty()
        tvVacio.visibility = if (vacia) View.VISIBLE else View.GONE
        rvRepetidas.visibility = if (vacia) View.GONE else View.VISIBLE
    }
}
