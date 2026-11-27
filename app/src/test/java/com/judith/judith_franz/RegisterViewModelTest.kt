package com.judith.judith_franz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val reglaLiveData = InstantTaskExecutorRule()

    private val viewModel = RegisterViewModel()

    @Test
    fun camposVacios_dan_error_obligatorio() {
        viewModel.registrar("", "", "", "")
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorNombre.value)
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorEmail.value)
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorPassword.value)
        assertEquals(R.string.error_campo_obligatorio, viewModel.errorConfirmar.value)
        assertFalse(viewModel.registroCorrecto.value!!)
    }

    @Test
    fun passwordCorta_da_error() {
        viewModel.registrar("Franz", "franz@iticbcn.cat", "123", "123")
        assertEquals(R.string.error_password_corta, viewModel.errorPassword.value)
        assertFalse(viewModel.registroCorrecto.value!!)
    }

    @Test
    fun passwordsDistintas_dan_error() {
        viewModel.registrar("Franz", "franz@iticbcn.cat", "123456", "654321")
        assertEquals(R.string.error_password_distinta, viewModel.errorConfirmar.value)
        assertFalse(viewModel.registroCorrecto.value!!)
    }

    @Test
    fun datosCorrectos_registro_correcto() {
        viewModel.registrar("Franz", "franz@iticbcn.cat", "123456", "123456")
        assertNull(viewModel.errorNombre.value)
        assertNull(viewModel.errorEmail.value)
        assertNull(viewModel.errorPassword.value)
        assertNull(viewModel.errorConfirmar.value)
        assertTrue(viewModel.registroCorrecto.value!!)
    }
}
