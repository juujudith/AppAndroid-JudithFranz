package com.judith.judith_franz

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Pantalla principal
class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var rvCartas: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var etFiltro: EditText
    private lateinit var spinnerRareza: Spinner
    private lateinit var spinnerTengo: Spinner

    private val adapter = CartaAdapter(
        alPulsar = { carta -> abrirDetalle(carta) },
        alMantener = { carta -> mostrarAcciones(carta) }
    )

    // Carga la lista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        findViewById<View>(R.id.main).ajustarABarrasYTeclado(
            ocultarConTeclado = findViewById(R.id.bottomBar)
        )

        BarraSuperior.configurar(this, conAtras = false) { id ->
            when (id) {
                R.id.accion_anadir -> {
                    abrirAnadir()
                    true
                }
                R.id.accion_repetidas -> {
                    startActivity(Intent(this, RepetidasActivity::class.java))
                    true
                }
                R.id.accion_ayuda -> {
                    startActivity(Intent(this, AyudaActivity::class.java))
                    true
                }
                else -> false
            }
        }
        BarraInferior.configurar(this, R.id.nav_home)

        rvCartas = findViewById(R.id.rvCartas)
        tvVacio = findViewById(R.id.tvHomeVacio)
        rvCartas.adapter = adapter
        ItemTouchHelper(DeslizarParaEliminar()).attachToRecyclerView(rvCartas)

        configurarFiltros()
    }

    override fun onResume() {
        super.onResume()
        etFiltro.clearFocus()
        ocultarTeclado()
        refrescar()
        BorradoCarta.mostrarAvisoPendiente(this, findViewById(R.id.main)) { refrescar() }
    }

    // Filtros de la lista
    private fun configurarFiltros() {
        etFiltro = findViewById(R.id.etFiltroCodigo)
        spinnerRareza = findViewById(R.id.spinnerFiltroRareza)
        spinnerTengo = findViewById(R.id.spinnerFiltroTengo)

        etFiltro.doAfterTextChanged { refrescar() }
        etFiltro.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_SEARCH) {
                buscarYCerrarTeclado()
                true
            } else {
                false
            }
        }
        findViewById<ImageButton>(R.id.btnBuscarFiltro).setOnClickListener { buscarYCerrarTeclado() }

        val alCambiar = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refrescar()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerRareza.onItemSelectedListener = alCambiar
        spinnerTengo.onItemSelectedListener = alCambiar
    }

    private fun buscarYCerrarTeclado() {
        etFiltro.clearFocus()
        ocultarTeclado()
        refrescar()
    }

    private fun ocultarTeclado() {
        WindowCompat.getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.ime())
    }

    private fun refrescar() {
        if (!::etFiltro.isInitialized) return
        val rareza = if (spinnerRareza.selectedItemPosition <= 0) {
            null
        } else {
            spinnerRareza.selectedItem?.toString()
        }
        val filtradas = FiltroCartas.filtrar(
            CartasRepositorio.todas(),
            etFiltro.text?.toString().orEmpty(),
            rareza,
            spinnerTengo.selectedItemPosition
        )
        adapter.submitList(filtradas)
        val vacia = filtradas.isEmpty()
        tvVacio.visibility = if (vacia) View.VISIBLE else View.GONE
        rvCartas.visibility = if (vacia) View.GONE else View.VISIBLE
    }

    private fun abrirDetalle(carta: Carta) {
        val intent = Intent(this, CardActivity::class.java)
            .putExtra(CardActivity.EXTRA_CODIGO, carta.codigo)
        startActivity(intent)
    }

    private fun abrirAnadir() {
        val intent = Intent(this, EditarCartaActivity::class.java)
            .putExtra(EditarCartaActivity.EXTRA_MODO_ANADIR, true)
        startActivity(intent)
    }

    private fun abrirModificar(carta: Carta) {
        val intent = Intent(this, EditarCartaActivity::class.java)
            .putExtra(CardActivity.EXTRA_CODIGO, carta.codigo)
        startActivity(intent)
    }

    private fun mostrarAcciones(carta: Carta) {
        MaterialAlertDialogBuilder(this)
            .setTitle(carta.nombre)
            .setItems(R.array.acciones_carta) { _, opcion ->
                when (opcion) {
                    0 -> abrirDetalle(carta)
                    1 -> abrirModificar(carta)
                    2 -> BorradoCarta.confirmar(this, carta, alConfirmar = { eliminar(carta) })
                }
            }
            .show()
    }

    // Borra la carta
    private fun eliminar(carta: Carta) {
        val posicion = CartasRepositorio.eliminar(carta.codigo)
        if (posicion == -1) {
            refrescar()
            return
        }
        Log.d(TAG, "Carta eliminada: ${carta.codigo} (${carta.nombre})")
        refrescar()
        BorradoCarta.mostrarAviso(this, findViewById(R.id.main), carta, posicion) { refrescar() }
    }

    private inner class DeslizarParaEliminar :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        private val pintura = Paint().apply {
            color = ContextCompat.getColor(this@HomeActivity, R.color.md_error_container)
        }
        private val icono = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_papelera)
            ?.mutate()
            ?.apply { setTint(ContextCompat.getColor(this@HomeActivity, R.color.md_on_error)) }
        private val margenIcono = resources.getDimensionPixelSize(R.dimen.espacio_xl)

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val posicion = viewHolder.bindingAdapterPosition
            if (posicion == RecyclerView.NO_POSITION) return
            val carta = adapter.currentList[posicion]
            BorradoCarta.confirmar(
                this@HomeActivity,
                carta,
                alConfirmar = { eliminar(carta) },
                alCancelar = { adapter.notifyItemChanged(posicion) }
            )
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX != 0f) {
                val fila = viewHolder.itemView
                val desplazamiento = dX.toInt()
                val fondo = if (dX > 0) {
                    Rect(fila.left, fila.top, fila.left + desplazamiento, fila.bottom)
                } else {
                    Rect(fila.right + desplazamiento, fila.top, fila.right, fila.bottom)
                }
                c.drawRect(fondo, pintura)

                icono?.let { dibujo ->
                    val ancho = dibujo.intrinsicWidth
                    val alto = dibujo.intrinsicHeight
                    if (fondo.width() > ancho + margenIcono * 2) {
                        val arriba = fila.top + (fila.height - alto) / 2
                        val izquierda = if (dX > 0) fila.left + margenIcono else fila.right - margenIcono - ancho
                        dibujo.setBounds(izquierda, arriba, izquierda + ancho, arriba + alto)
                        dibujo.draw(c)
                    }
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}
