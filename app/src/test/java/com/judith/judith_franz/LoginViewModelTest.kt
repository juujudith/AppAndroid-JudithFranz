package com.judith.judith_franz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    // Hace que LiveData funcione en tests
    @get:Rule
    val reglaLiveData = InstantTaskExecutorRule()

    private val viewModel = LoginViewModel()

    @Test
    fun camposVacios_dan_error_obligatorio() {
        viewModel.entrar("", "")
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorEmail.value)
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorPassword.value)
        assertFalse(viewModel.loginCorrecto.value!!)
    }

    @Test
    fun emailSinArroba_da_error_email_invalido() {
        viewModel.entrar("franz.correo.com", "123456")
        assertEquals(R.string.error_email_invalido, viewModel.errorEmail.value)
        assertFalse(viewModel.loginCorrecto.value!!)
    }

    @Test
    fun datosCorrectos_login_correcto() {
        viewModel.entrar("franz@iticbcn.cat", "123456")
        assertNull(viewModel.errorEmail.value)
        assertNull(viewModel.errorPassword.value)
        assertTrue(viewModel.loginCorrecto.value!!)
    }

    @Test
    fun loginConsumido_vuelve_a_false() {
        viewModel.entrar("franz@iticbcn.cat", "123456")
        viewModel.loginConsumido()
        assertFalse(viewModel.loginCorrecto.value!!)
    }
}
