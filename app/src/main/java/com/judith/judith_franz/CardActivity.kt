package com.judith.judith_franz

// Act. 05: pantalla de detalle de una carta, solo interfaz y navegación.

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BarraInferior.configurar(this, R.id.nav_home)

        findViewById<ImageButton>(R.id.btnVolverHome).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnModificarCarta).setOnClickListener {
            startActivity(Intent(this, EditarCartaActivity::class.java))
        }
    }
}
