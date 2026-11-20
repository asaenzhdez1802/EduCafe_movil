package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataArticuloCarrito
import com.example.educafe.R
import com.example.educafe.Vista.TuPedidoActivity

class CarritoAdaptador(
    private var articulos: List<dataArticuloCarrito>,
    private val activity: TuPedidoActivity
) : RecyclerView.Adapter<CarritoAdaptador.ArticuloViewHolder>() {

    class ArticuloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgArticulo: ImageView = itemView.findViewById(R.id.imgArticulo)
        val txtNombreArticulo: TextView = itemView.findViewById(R.id.txtNombreArticulo)
        val txtPrecioArticulo: TextView = itemView.findViewById(R.id.txtPrecioArticulo)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_articulo_pedido, parent, false)
        return ArticuloViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = articulos[position]

        holder.txtNombreArticulo.text = articulo.nombre
        holder.txtCantidad.text = articulo.cantidad.toString()
        if (articulo.cantidad > 1) {
            holder.txtPrecioArticulo.text =
                "${clsFormato.dinero(articulo.precio, false)} c/u · ${clsFormato.dinero(articulo.subtotal())}"
        } else {
            holder.txtPrecioArticulo.text = clsFormato.dinero(articulo.precio)
        }

        holder.imgArticulo.setImageDrawable(null)


        val limite = minOf(clsCarrito.MAXIMO_POR_PRODUCTO, articulo.disponibles)
        val puedeAumentar = articulo.cantidad < limite
        holder.btnMas.alpha = if (puedeAumentar) 1f else 0.4f

        holder.btnMas.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    activity.aumentar(articulo)
                }
            }
        )

        holder.btnMenos.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    activity.disminuir(articulo)
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return articulos.size
    }

    fun actualizarArticulos(nuevosArticulos: List<dataArticuloCarrito>) {
        articulos = nuevosArticulos
        notifyDataSetChanged()
    }
}
