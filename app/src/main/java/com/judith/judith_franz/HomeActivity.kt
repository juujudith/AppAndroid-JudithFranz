package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private val adapter = CartaAdapter { carta ->
        val intent = Intent(this, CardActivity::class.java)
            .putExtra(CardActivity.EXTRA_CODIGO, carta.codigo)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BarraInferior.configurar(this, R.id.nav_home)

        findViewById<RecyclerView>(R.id.rvCartas).adapter = adapter

        findViewById<ImageButton>(R.id.btnAnadirCarta).setOnClickListener {
            val intent = Intent(this, EditarCartaActivity::class.java)
                .putExtra(EditarCartaActivity.EXTRA_MODO_ANADIR, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(CartasRepositorio.todas())
    }
}
