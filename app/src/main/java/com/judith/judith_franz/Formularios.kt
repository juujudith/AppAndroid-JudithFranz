package com.judith.judith_franz

import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.limpiarErrorAlEscribir() {
    editText?.doAfterTextChanged {
        if (error != null) {
            error = null
            isErrorEnabled = false
        }
    }
}

fun TextInputLayout.texto(): String = editText?.text?.toString().orEmpty()
