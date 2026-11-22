package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.educafe.R
import java.net.URI

// Datos preparados para una fila visual.
// No representa directamente una respuesta de la API.
data class FilaReporte(
    val titulo: String,
    val detalle: String = "",
    val importe: String = "",
    val imagen: String? = null,
    val mostrarImagen: Boolean = false,
    val porcentaje: Int? = null
)

class ReportesAdminAdaptador :
    RecyclerView.Adapter<ReportesAdminAdaptador.FilaHolder>() {

    private var filas = emptyList<FilaReporte>()

    class FilaHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val titulo: TextView =
            vista.findViewById(R.id.txtTituloFilaAdmin)
        val detalle: TextView =
            vista.findViewById(R.id.txtDetalleFilaAdmin)
        val importe: TextView =
            vista.findViewById(R.id.txtImporteFilaAdmin)
        val imagen: ImageView =
            vista.findViewById(R.id.imgFilaAdmin)
        val barra: ProgressBar =
            vista.findViewById(R.id.barraFilaAdmin)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilaHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte_admin, parent, false)

        return FilaHolder(vista)
    }

    override fun onBindViewHolder(
        holder: FilaHolder,
        position: Int
    ) {
        val fila = filas[position]

        holder.titulo.text = fila.titulo
        holder.detalle.text = fila.detalle
        holder.importe.text = fila.importe

        holder.detalle.visibility =
            if (fila.detalle.isBlank()) View.GONE else View.VISIBLE

        holder.barra.visibility =
            if (fila.porcentaje == null) View.GONE else View.VISIBLE

        holder.barra.progress =
            (fila.porcentaje ?: 0).coerceIn(0, 100)

        holder.imagen.visibility =
            if (fila.mostrarImagen) View.VISIBLE else View.GONE

        // Limpiar la imagen anterior cuando se reutiliza una fila.
        Glide.with(holder.imagen).clear(holder.imagen)

        if (fila.mostrarImagen) {
            val url = fila.imagen?.takeIf { it.isNotBlank() }?.let {
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
    }

    override fun getItemCount(): Int {
        return filas.size
    }

    fun actualizar(datos: List<FilaReporte>) {
        filas = datos
        notifyDataSetChanged()
    }
}