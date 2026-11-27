package com.judith.judith_franz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel del login: valida y avisa con LiveData
class LoginViewModel : ViewModel() {

    // Las MutableLiveData solo las cambia el ViewModel
    private val _errorEmail = MutableLiveData<Int?>()
    private val _errorPassword = MutableLiveData<Int?>()
    private val _loginCorrecto = MutableLiveData(false)

    // La pantalla solo puede leerlas
    val errorEmail: LiveData<Int?> = _errorEmail
    val errorPassword: LiveData<Int?> = _errorPassword
    val loginCorrecto: LiveData<Boolean> = _loginCorrecto

    // Comprueba los campos y guarda el resultado
    fun entrar(email: String, password: String) {
        _errorEmail.value = when {
            email.isEmpty() -> R.string.error_campo_obligatorio
            !esEmailValido(email) -> R.string.error_email_invalido
            else -> null
        }
        _errorPassword.value = if (password.isEmpty()) R.string.error_campo_obligatorio else null
        _loginCorrecto.value = _errorEmail.value == null && _errorPassword.value == null
    }

    fun borrarErrorEmail() {
        _errorEmail.value = null
    }

    fun borrarErrorPassword() {
        _errorPassword.value = null
    }

    // La pantalla lo llama al navegar, para no repetirlo al girar
    fun loginConsumido() {
        _loginCorrecto.value = false
    }
}
