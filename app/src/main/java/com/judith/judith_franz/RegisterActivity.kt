package com.judith.judith_franz

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import android.widget.TextView         // NUEVO: para usar el texto "Inicia sesión"
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

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

        findViewById<Button>(R.id.btnRegistro).setOnClickListener { registrar() }
    }

    private fun registrar() {
        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmailRegistro)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPasswordRegistro)
        val tilConfirmar = findViewById<TextInputLayout>(R.id.tilConfirmarPassword)

        val nombre = tilNombre.editText?.text?.toString()?.trim().orEmpty()
        val email = tilEmail.editText?.text?.toString()?.trim().orEmpty()
        val password = tilPassword.editText?.text?.toString().orEmpty()
        val confirmar = tilConfirmar.editText?.text?.toString().orEmpty()

        tilNombre.error = if (nombre.isEmpty()) getString(R.string.error_campo_obligatorio) else null
        tilEmail.error = when {
            email.isEmpty() -> getString(R.string.error_campo_obligatorio)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalido)
            else -> null
        }
        tilPassword.error = when {
            password.isEmpty() -> getString(R.string.error_campo_obligatorio)
            password.length < 6 -> getString(R.string.error_password_corta)
            else -> null
        }
        tilConfirmar.error = when {
            confirmar.isEmpty() -> getString(R.string.error_campo_obligatorio)
            confirmar != password -> getString(R.string.error_password_distinta)
            else -> null
        }

        val hayErrores = listOf(tilNombre, tilEmail, tilPassword, tilConfirmar).any { it.error != null }
        if (hayErrores) return

        Toast.makeText(this, R.string.registro_correcto, Toast.LENGTH_SHORT).show()
        finish()
    }
}