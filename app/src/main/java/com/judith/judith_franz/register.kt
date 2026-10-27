package com.judith.judith_franz

import android.os.Bundle
import android.widget.TextView         // NUEVO: para usar el texto "Inicia sesión"
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // NUEVO: "¿Ya tienes cuenta? Inicia sesión" --> vuelve al login
        val tvYaCuenta = findViewById<TextView>(R.id.tvYaCuenta)

        tvYaCuenta.setOnClickListener {
            finish()
        }
    }
}