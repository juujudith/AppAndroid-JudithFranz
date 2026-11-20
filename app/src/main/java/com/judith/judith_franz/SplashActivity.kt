package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// Pantalla de inicio
class SplashActivity : AppCompatActivity() {

    private var loginAbierto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (animacionesDesactivadas()) {
            abrirLogin()
            return
        }

        val logo = findViewById<ImageView>(R.id.ivLogoSplash)
        // Animacion del logo
        val animacion = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
        animacion.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                abrirLogin()
            }
        })
        logo.startAnimation(animacion)
    }

    private fun animacionesDesactivadas(): Boolean {
        val escala = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        return escala == 0f
    }

    private fun abrirLogin() {
        if (loginAbierto || isFinishing) return
        loginAbierto = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
