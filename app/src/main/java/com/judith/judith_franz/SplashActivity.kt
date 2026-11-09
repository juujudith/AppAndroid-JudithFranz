package com.judith.judith_franz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.ivLogoSplash)

        logo.alpha = 0f
        logo.scaleX = 0.5f
        logo.scaleY = 0.5f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1500)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}