package com.judith.judith_franz

import android.content.Intent          // NUEVO: para cambiar de pantalla
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado()

        // ¿No tienes cuenta? Regístrate --> RegistroActivity
        // Registrarse
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<TextView>(R.id.tvAyuda).setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }
    }
}
