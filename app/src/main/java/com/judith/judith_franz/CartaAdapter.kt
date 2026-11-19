package com.judith.judith_franz


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.color.MaterialColors
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CartaAdapter(
    private val alPulsar: (Carta) -> Unit
) : ListAdapter<Carta, CartaAdapter.CartaViewHolder>(Diferencias) {

    class CartaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        private val ivImagen: ImageView = vista.findViewById(R.id.ivItemCarta)
        private val tvNombre: TextView = vista.findViewById(R.id.tvItemNombre)
        private val tvCodigo: TextView = vista.findViewById(R.id.tvItemCodigo)
        private val tvRareza: TextView = vista.findViewById(R.id.tvItemRareza)
        private val tvEstado: TextView = vista.findViewById(R.id.tvItemEstado)

        fun mostrar(carta: Carta, alPulsar: (Carta) -> Unit) {
            val contexto = itemView.context

            ivImagen.cargarCarta(carta)
            ivImagen.contentDescription =
                contexto.getString(R.string.desc_imagen_carta, carta.nombre, carta.codigo)
            ivImagen.alpha = if (carta.tengo) 1f else 0.55f

            tvNombre.text = carta.nombre
            tvCodigo.text = carta.codigo
            tvRareza.text = carta.rareza

            if (carta.tengo) {
                tvEstado.setText(R.string.estado_tengo)
                tvEstado.setTextColor(MaterialColors.getColor(tvEstado, R.attr.temaOk))
            } else {
                tvEstado.setText(R.string.estado_falta)
                tvEstado.setTextColor(MaterialColors.getColor(tvEstado, R.attr.temaFalta))
            }

            itemView.setOnClickListener { alPulsar(carta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_carta, parent, false)
        return CartaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        holder.mostrar(getItem(position), alPulsar)
    }

    private object Diferencias : DiffUtil.ItemCallback<Carta>() {
        override fun areItemsTheSame(antigua: Carta, nueva: Carta) = antigua.codigo == nueva.codigo
        override fun areContentsTheSame(antigua: Carta, nueva: Carta) = antigua == nueva
    }
}
