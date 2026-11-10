package com.judith.judith_franz

// Act. 05: pantalla para editar o añadir una carta, solo interfaz y navegación.

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarCartaActivity : AppCompatActivity() {

    companion object {
        /** true = la pantalla se abre para añadir una carta nueva. */
        const val EXTRA_MODO_ANADIR = "EXTRA_MODO_ANADIR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_carta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.getBooleanExtra(EXTRA_MODO_ANADIR, false)) {
            findViewById<TextView>(R.id.tvEditarTitulo).setText(R.string.anadir_titulo)
        }

        findViewById<ImageButton>(R.id.btnVolverEditar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancelarEditar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnGuardarCarta).setOnClickListener { finish() }
    }
}
