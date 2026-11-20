package com.judith.judith_franz

import android.content.Intent          // NUEVO: para cambiar de pantalla
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

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

        findViewById<Button>(R.id.btnLogin).setOnClickListener { entrar() }

        findViewById<TextView>(R.id.tvAyuda).setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }
    }

    private fun entrar() {
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val email = tilEmail.editText?.text?.toString()?.trim().orEmpty()
        val password = tilPassword.editText?.text?.toString().orEmpty()

        tilEmail.error = when {
            email.isEmpty() -> getString(R.string.error_campo_obligatorio)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalido)
            else -> null
        }
        tilPassword.error = if (password.isEmpty()) getString(R.string.error_campo_obligatorio) else null

        if (tilEmail.error != null || tilPassword.error != null) return

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
