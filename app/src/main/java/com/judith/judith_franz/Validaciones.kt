package com.judith.judith_franz

// Comprueba que el email tiene forma de email (funciona también en tests)
private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun esEmailValido(email: String): Boolean = EMAIL_REGEX.matches(email)
