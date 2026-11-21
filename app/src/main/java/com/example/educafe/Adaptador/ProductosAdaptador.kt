package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataProductos
import com.example.educafe.R
import com.google.android.material.button.MaterialButton
import java.net.URI

class ProductosAdaptador(
    private val agregar: (dataProductos) -> Unit
) : RecyclerView.Adapter<ProductosAdaptador.ProductoViewHolder>() {

    private var productos = emptyList<dataProductos>()

    class ProductoViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val imagen: ImageView = vista.findViewById(R.id.imgProductoMenu)
        val nombre: TextView = vista.findViewById(R.id.txtNombreProductoMenu)
        val precio: TextView = vista.findViewById(R.id.txtPrecioProductoMenu)
        val estado: TextView = vista.findViewById(R.id.txtEstadoProductoMenu)
        val boton: MaterialButton =
            vista.findViewById(R.id.btnAgregarProductoMenu)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_menu, parent, false)

        return ProductoViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: ProductoViewHolder,
        position: Int
    ) {
        val producto = productos[position]

        holder.nombre.text = producto.nombre
        holder.precio.text = clsFormato.dinero(producto.precioVenta)

        holder.estado.visibility =
            if (producto.disponibles > 0) View.GONE else View.VISIBLE

        holder.boton.isEnabled = producto.disponibles > 0
        holder.boton.alpha = if (producto.disponibles > 0) 1f else 0.4f
        holder.boton.contentDescription = "Agregar ${producto.nombre}"

        holder.boton.setOnClickListener {
            agregar(producto)
        }

        // Aceptar una ruta relativa o una URL completa de imagen.
        val url = producto.imagen?.takeIf { it.isNotBlank() }?.let {
            runCatching {
                URI("https://manuelsh.somos4a.org/educafe_api/")
                    .resolve(it)
                    .toString()
            }.getOrNull()
        }

        Glide.with(holder.imagen)
            .load(url)
            .placeholder(R.drawable.ic_productos)
            .error(R.drawable.ic_productos)
            .centerCrop()
            .into(holder.imagen)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    fun actualizar(nuevos: List<dataProductos>) {
        productos = nuevos
        notifyDataSetChanged()
    }
}