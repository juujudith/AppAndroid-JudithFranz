package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

// Login de la app
class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        configurarSplash(splash, savedInstanceState == null && intent?.action == Intent.ACTION_MAIN)
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

        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        tilEmail.limpiarErrorAlEscribir()
        tilPassword.limpiarErrorAlEscribir()
        tilEmail.editText?.doAfterTextChanged { viewModel.borrarErrorEmail() }
        tilPassword.editText?.doAfterTextChanged { viewModel.borrarErrorPassword() }
        observarViewModel(tilEmail, tilPassword)
        findViewById<Button>(R.id.btnLogin).setOnClickListener { entrar() }

        findViewById<TextView>(R.id.tvAyuda).setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }
    }

    private fun configurarSplash(
        splash: SplashScreen,
        esArranque: Boolean
    ) {
        val sinAnimaciones = animacionesDesactivadas()
        if (esArranque && !sinAnimaciones) {
            val limite = SystemClock.uptimeMillis() +
                resources.getInteger(R.integer.tiempo_minimo_splash)
            splash.setKeepOnScreenCondition { SystemClock.uptimeMillis() < limite }
        }
        splash.setOnExitAnimationListener { proveedor ->
            if (sinAnimaciones) {
                proveedor.remove()
            } else {
                animarSalidaSplash(proveedor)
            }
        }
    }

    private fun animarSalidaSplash(proveedor: SplashScreenViewProvider) {
        val duracion = resources.getInteger(R.integer.duracion_splash_salida).toLong()
        proveedor.iconView.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .alpha(0f)
            .setDuration(duracion)
            .start()
        proveedor.view.animate()
            .alpha(0f)
            .setDuration(duracion)
            .withEndAction { proveedor.remove() }
            .start()
    }

    private fun animacionesDesactivadas(): Boolean {
        val escala = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        return escala == 0f
    }

    // Escucha lo que dice el ViewModel
    private fun observarViewModel(tilEmail: TextInputLayout, tilPassword: TextInputLayout) {
        viewModel.errorEmail.observe(this) { id ->
            tilEmail.error = id?.let { getString(it) }
        }
        viewModel.errorPassword.observe(this) { id ->
            tilPassword.error = id?.let { getString(it) }
        }
        viewModel.loginCorrecto.observe(this) { correcto ->
            if (correcto) {
                viewModel.loginConsumido()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    // Entra con el login
    private fun entrar() {
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val email = tilEmail.editText?.text?.toString()?.trim().orEmpty()
        val password = tilPassword.editText?.text?.toString().orEmpty()

        viewModel.entrar(email, password)

        val primerError = listOf(tilEmail, tilPassword).firstOrNull { it.error != null }
        if (primerError != null) {
            mostrarJuntos(primerError, findViewById(R.id.btnLogin))
        }
    }
}
