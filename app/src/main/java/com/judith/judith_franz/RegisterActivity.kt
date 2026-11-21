package com.judith.judith_franz

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

// Crear cuenta
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado(
            mostrarConTeclado = findViewById(R.id.btnRegistro)
        )

        val tvYaCuenta = findViewById<TextView>(R.id.tvYaCuenta)

        tvYaCuenta.setOnClickListener {
            finish()
        }

        listOf(R.id.tilNombre, R.id.tilEmailRegistro, R.id.tilPasswordRegistro, R.id.tilConfirmarPassword)
            .forEach { findViewById<TextInputLayout>(it).limpiarErrorAlEscribir() }
        findViewById<Button>(R.id.btnRegistro).setOnClickListener { registrar() }
    }

    // Crea la cuenta
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

        val primerError = listOf(tilNombre, tilEmail, tilPassword, tilConfirmar).firstOrNull { it.error != null }
        if (primerError != null) {
            mostrarJuntos(primerError, findViewById(R.id.btnRegistro))
            return
        }

        Toast.makeText(this, R.string.registro_correcto, Toast.LENGTH_SHORT).show()
        finish()
    }
}