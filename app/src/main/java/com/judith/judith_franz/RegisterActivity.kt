package com.judith.judith_franz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

// Crear cuenta
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

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
        findViewById<TextInputLayout>(R.id.tilNombre).editText?.doAfterTextChanged { viewModel.borrarErrorNombre() }
        findViewById<TextInputLayout>(R.id.tilEmailRegistro).editText?.doAfterTextChanged { viewModel.borrarErrorEmail() }
        findViewById<TextInputLayout>(R.id.tilPasswordRegistro).editText?.doAfterTextChanged { viewModel.borrarErrorPassword() }
        findViewById<TextInputLayout>(R.id.tilConfirmarPassword).editText?.doAfterTextChanged { viewModel.borrarErrorConfirmar() }
        observarViewModel()
        findViewById<Button>(R.id.btnRegistro).setOnClickListener { registrar() }
    }

    // Escucha lo que dice el ViewModel
    private fun observarViewModel() {
        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmailRegistro)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPasswordRegistro)
        val tilConfirmar = findViewById<TextInputLayout>(R.id.tilConfirmarPassword)

        viewModel.errorNombre.observe(this) { id -> tilNombre.error = id?.let { getString(it) } }
        viewModel.errorEmail.observe(this) { id -> tilEmail.error = id?.let { getString(it) } }
        viewModel.errorPassword.observe(this) { id -> tilPassword.error = id?.let { getString(it) } }
        viewModel.errorConfirmar.observe(this) { id -> tilConfirmar.error = id?.let { getString(it) } }
        viewModel.registroCorrecto.observe(this) { correcto ->
            if (correcto) {
                viewModel.registroConsumido()
                Toast.makeText(this, R.string.registro_correcto, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
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

        viewModel.registrar(nombre, email, password, confirmar)

        val primerError = listOf(tilNombre, tilEmail, tilPassword, tilConfirmar).firstOrNull { it.error != null }
        if (primerError != null) {
            mostrarJuntos(primerError, findViewById(R.id.btnRegistro))
        }
    }
}