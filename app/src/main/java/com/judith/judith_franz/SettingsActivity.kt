package com.judith.judith_franz


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch

// Ajustes de la app
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BarraSuperior.configurar(this, conAtras = false)
        BarraInferior.configurar(this, R.id.nav_settings)

        val swGuardarAuto = findViewById<MaterialSwitch>(R.id.swGuardarAuto)
        swGuardarAuto.isChecked = PreferenciasApp.guardarAutomaticamente(this)
        swGuardarAuto.setOnCheckedChangeListener { _, marcado ->
            PreferenciasApp.ponerGuardarAutomaticamente(this, marcado)
        }

        val swVibracion = findViewById<MaterialSwitch>(R.id.swVibracion)
        swVibracion.isChecked = PreferenciasApp.vibrar(this)
        swVibracion.setOnCheckedChangeListener { _, marcado ->
            PreferenciasApp.ponerVibrar(this, marcado)
        }

        findViewById<View>(R.id.optRepetidas).setOnClickListener {
            startActivity(Intent(this, RepetidasActivity::class.java))
        }

        findViewById<View>(R.id.optAyuda).setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }

        findViewById<View>(R.id.optTema).setOnClickListener {
            startActivity(Intent(this, TemasActivity::class.java))
        }

        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    // Pone el tema actual
    override fun onResume() {
        super.onResume()
        val temaActual = Temas.actual(this)
        findViewById<TextView>(R.id.tvTemaActual).setText(temaActual.nombre)
        findViewById<View>(R.id.optTema).contentDescription =
            getString(R.string.desc_opt_tema, getString(temaActual.nombre))
    }
}
