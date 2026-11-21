package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

// Login de la app
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado(
            mostrarConTeclado = findViewById(R.id.btnLogin)
        )

        // ¿No tienes cuenta? Regístrate --> RegisterActivity
        // Registrarse
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextInputLayout>(R.id.tilEmail).limpiarErrorAlEscribir()
        findViewById<TextInputLayout>(R.id.tilPassword).limpiarErrorAlEscribir()
        findViewById<Button>(R.id.btnLogin).setOnClickListener { entrar() }

        findViewById<TextView>(R.id.tvAyuda).setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }
    }

    // Entra con el login
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

        val primerError = listOf(tilEmail, tilPassword).firstOrNull { it.error != null }
        if (primerError != null) {
            mostrarJuntos(primerError, findViewById(R.id.btnLogin))
            return
        }

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
