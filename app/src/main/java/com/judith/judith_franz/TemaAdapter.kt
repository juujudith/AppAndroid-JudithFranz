package com.judith.judith_franz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class TemaAdapter(
    private val alElegir: (Temas.Tema) -> Unit
) : ListAdapter<TemaAdapter.TemaItem, TemaAdapter.TemaViewHolder>(Diferencias) {

    data class TemaItem(val tema: Temas.Tema, val enUso: Boolean)

    class TemaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        private val tarjeta = vista as MaterialCardView
        private val tvNombre: TextView = vista.findViewById(R.id.tvTemaNombre)
        private val tvModo: TextView = vista.findViewById(R.id.tvTemaModo)
        private val marcaEnUso: View = vista.findViewById(R.id.tvTemaActualMarca)

        fun mostrar(item: TemaItem, alElegir: (Temas.Tema) -> Unit) {
            val contexto = itemView.context
            val nombre = contexto.getString(item.tema.nombre)
            val modo = contexto.getString(if (item.tema.oscuro) R.string.tema_oscuro else R.string.tema_claro)

            tvNombre.text = nombre
            tvModo.text = modo
            marcaEnUso.visibility = if (item.enUso) View.VISIBLE else View.GONE
            tarjeta.strokeWidth = if (item.enUso) {
                contexto.resources.getDimensionPixelSize(R.dimen.grosor_seleccion_tema)
            } else {
                0
            }
            tarjeta.contentDescription = if (item.enUso) {
                contexto.getString(R.string.desc_tema_en_uso, nombre)
            } else {
                contexto.getString(R.string.desc_tema, nombre)
            }
            tarjeta.isSelected = item.enUso
            tarjeta.setOnClickListener {
                if (!item.enUso) alElegir(item.tema)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = Temas.lista.indexOf(getItem(position).tema)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemaViewHolder {
        val tema = Temas.lista[viewType]
        val conColoresDelTema = ContextThemeWrapper(parent.context, tema.estilo)
        val vista = LayoutInflater.from(conColoresDelTema).inflate(R.layout.item_tema, parent, false)
        return TemaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: TemaViewHolder, position: Int) {
        holder.mostrar(getItem(position), alElegir)
    }

    private object Diferencias : DiffUtil.ItemCallback<TemaItem>() {
        override fun areItemsTheSame(antiguo: TemaItem, nuevo: TemaItem) = antiguo.tema.id == nuevo.tema.id
        override fun areContentsTheSame(antiguo: TemaItem, nuevo: TemaItem) = antiguo == nuevo
    }
}
