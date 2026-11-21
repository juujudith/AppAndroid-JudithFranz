package com.judith.judith_franz


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.card.MaterialCardView

// Elegir tema
class TemasActivity : AppCompatActivity() {

    // Carga los temas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temas)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado()

        BarraSuperior.configurar(this, conAtras = true)

        val grid = findViewById<GridLayout>(R.id.gridTemas)
        val actual = Temas.actual(this)
        for (tema in Temas.lista) {
            grid.addView(crearTarjeta(grid, tema, tema == actual))
        }
    }

    // Crea cada tarjeta
    private fun crearTarjeta(grid: GridLayout, tema: Temas.Tema, enUso: Boolean): View {
        val conColoresDelTema = ContextThemeWrapper(this, tema.estilo)
        val tarjeta = LayoutInflater.from(conColoresDelTema)
            .inflate(R.layout.item_tema, grid, false) as MaterialCardView

        tarjeta.findViewById<TextView>(R.id.tvTemaNombre).setText(tema.nombre)
        tarjeta.findViewById<TextView>(R.id.tvTemaModo)
            .setText(if (tema.oscuro) R.string.tema_oscuro else R.string.tema_claro)
        tarjeta.contentDescription = getString(tema.nombre)

        if (enUso) {
            tarjeta.strokeWidth = (3 * resources.displayMetrics.density).toInt()
            tarjeta.findViewById<View>(R.id.tvTemaActualMarca).visibility = View.VISIBLE
        }

        tarjeta.setOnClickListener {
            if (!enUso) {
                Temas.guardar(this, tema)
                recreate()
            }
        }

        val margen = (6 * resources.displayMetrics.density).toInt()
        tarjeta.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        ).apply {
            width = 0
            setMargins(margen, margen, margen, margen)
        }
        return tarjeta
    }
}
