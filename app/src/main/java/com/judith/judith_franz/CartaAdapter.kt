package com.judith.judith_franz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CartaAdapter(
    private val onCartaClick: (Carta) -> Unit
) : RecyclerView.Adapter<CartaAdapter.CartaViewHolder>() {

    private var cartas: List<Carta> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun actualizar(nuevasCartas: List<Carta>) {
        cartas = nuevasCartas
        notifyDataSetChanged()
    }

    class CartaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val imagen: ImageView = vista.findViewById(R.id.ivItemCarta)
        val nombre: TextView = vista.findViewById(R.id.tvItemNombre)
        val codigo: TextView = vista.findViewById(R.id.tvItemCodigo)
        val rareza: TextView = vista.findViewById(R.id.tvItemRareza)
        val estado: TextView = vista.findViewById(R.id.tvItemEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carta, parent, false)
        return CartaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        val carta = cartas[position]
        val contexto = holder.itemView.context

        holder.imagen.setImageResource(carta.imagen)
        holder.imagen.contentDescription =
            contexto.getString(R.string.desc_imagen_carta, carta.nombre, carta.codigo)
        holder.imagen.alpha = if (carta.tengo) 1f else 0.55f

        holder.nombre.text = carta.nombre
        holder.codigo.text = carta.codigo
        holder.rareza.text = carta.rareza

        if (carta.tengo) {
            holder.estado.setText(R.string.estado_tengo)
            holder.estado.setTextColor(ContextCompat.getColor(contexto, R.color.verde_ok))
        } else {
            holder.estado.setText(R.string.estado_falta)
            holder.estado.setTextColor(ContextCompat.getColor(contexto, R.color.rojo_error))
        }

        holder.itemView.setOnClickListener { onCartaClick(carta) }
    }

    override fun getItemCount(): Int = cartas.size
}