package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataArticuloCarrito
import com.example.educafe.R
import com.example.educafe.Vista.TuPedidoActivity

class CarritoAdaptador(
    private var articulos: List<dataArticuloCarrito>,
    private val activity: TuPedidoActivity
) : RecyclerView.Adapter<CarritoAdaptador.ArticuloViewHolder>() {

    companion object {

        private const val URL_IMAGENES = "https://manuelsh.somos4a.org/educafe_api/"
    }

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
        // Precio unitario, como en el mockup: "$45.00 MXN"
        holder.txtPrecioArticulo.text = clsFormato.dinero(articulo.precio)

        // Imagen: URL base + ruta que manda la API (img/productos/capuchino.jpg)
        val urlImagen = if (articulo.imagen.isNullOrEmpty()) null else URL_IMAGENES + articulo.imagen
        val radio = (12 * holder.itemView.resources.displayMetrics.density).toInt()
        Glide.with(holder.itemView.context)
            .load(urlImagen)
            .transform(CenterCrop(), RoundedCorners(radio))
            .placeholder(R.drawable.bg_crema_12)
            .error(R.drawable.bg_crema_12)
            .into(holder.imgArticulo)

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
