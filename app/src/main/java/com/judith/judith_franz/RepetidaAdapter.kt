package com.judith.judith_franz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Adapter repetidas
class RepetidaAdapter(
    private val alPulsar: (Carta) -> Unit
) : ListAdapter<Carta, RepetidaAdapter.RepetidaViewHolder>(Diferencias) {

    class RepetidaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        private val ivImagen: ImageView = vista.findViewById(R.id.ivRepetidaImagen)
        private val tvNombre: TextView = vista.findViewById(R.id.tvRepetidaNombre)
        private val tvCodigo: TextView = vista.findViewById(R.id.tvRepetidaCodigo)
        private val tvCantidad: TextView = vista.findViewById(R.id.tvRepetidaCantidad)

        fun mostrar(carta: Carta, alPulsar: (Carta) -> Unit) {
            val contexto = itemView.context

            ivImagen.cargarCarta(carta)
            tvNombre.text = carta.nombre
            tvCodigo.text = carta.codigo
            tvCantidad.text = contexto.getString(R.string.repetidas_cantidad, carta.cantidad)

            itemView.contentDescription = contexto.getString(
                R.string.desc_item_repetida, carta.nombre, carta.codigo, carta.cantidad
            )
            // Al pulsar
            itemView.setOnClickListener { alPulsar(carta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepetidaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_repetida, parent, false)
        return RepetidaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: RepetidaViewHolder, position: Int) {
        holder.mostrar(getItem(position), alPulsar)
    }

    private object Diferencias : DiffUtil.ItemCallback<Carta>() {
        override fun areItemsTheSame(antigua: Carta, nueva: Carta) = antigua.codigo == nueva.codigo
        override fun areContentsTheSame(antigua: Carta, nueva: Carta) = antigua == nueva
    }
}
