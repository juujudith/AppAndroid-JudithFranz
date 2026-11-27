package com.judith.judith_franz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel del registro: valida los 4 campos y avisa con LiveData
class RegisterViewModel : ViewModel() {

    private val _errorNombre = MutableLiveData<Int?>()
    private val _errorEmail = MutableLiveData<Int?>()
    private val _errorPassword = MutableLiveData<Int?>()
    private val _errorConfirmar = MutableLiveData<Int?>()
    private val _registroCorrecto = MutableLiveData(false)

    val errorNombre: LiveData<Int?> = _errorNombre
    val errorEmail: LiveData<Int?> = _errorEmail
    val errorPassword: LiveData<Int?> = _errorPassword
    val errorConfirmar: LiveData<Int?> = _errorConfirmar
    val registroCorrecto: LiveData<Boolean> = _registroCorrecto

    // Comprueba los campos y guarda el resultado
    fun registrar(nombre: String, email: String, password: String, confirmar: String) {
        _errorNombre.value = if (nombre.isEmpty()) R.string.error_campo_obligatorio else null
        _errorEmail.value = when {
            email.isEmpty() -> R.string.error_campo_obligatorio
            !esEmailValido(email) -> R.string.error_email_invalido
            else -> null
        }
        _errorPassword.value = when {
            password.isEmpty() -> R.string.error_campo_obligatorio
            password.length < 6 -> R.string.error_password_corta
            else -> null
        }
        _errorConfirmar.value = when {
            confirmar.isEmpty() -> R.string.error_campo_obligatorio
            confirmar != password -> R.string.error_password_distinta
            else -> null
        }
        _registroCorrecto.value = listOf(
            _errorNombre.value, _errorEmail.value, _errorPassword.value, _errorConfirmar.value
        ).all { it == null }
    }

    fun borrarErrorNombre() { _errorNombre.value = null }
    fun borrarErrorEmail() { _errorEmail.value = null }
    fun borrarErrorPassword() { _errorPassword.value = null }
    fun borrarErrorConfirmar() { _errorConfirmar.value = null }

    // La pantalla lo llama al terminar, para no repetirlo al girar
    fun registroConsumido() {
        _registroCorrecto.value = false
    }
}
