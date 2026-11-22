package com.judith.judith_franz


import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

// Elegir tema
class TemasActivity : AppCompatActivity() {

    // Carga los temas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temas)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado()

        BarraSuperior.configurar(this, conAtras = true)

        val actual = Temas.actual(this)
        val adapter = TemaAdapter { tema ->
            Temas.guardar(this, tema)
            recreate()
        }
        findViewById<RecyclerView>(R.id.rvTemas).adapter = adapter
        adapter.submitList(Temas.lista.map { TemaAdapter.TemaItem(it, it.id == actual.id) })
    }
}
