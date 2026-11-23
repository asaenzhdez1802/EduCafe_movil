package com.example.educafe.Adaptador

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Modelo.dataVentaCajero
import java.text.NumberFormat
import java.util.Locale

// Adaptador K4 - Historial de ventas del cajero
class clsMisVentasCajeroAdaptador(
    private val alSeleccionarVenta: (dataVentaCajero) -> Unit
) : RecyclerView.Adapter<clsMisVentasCajeroAdaptador.VistaVenta>() {

    private var listaVentas: List<dataVentaCajero> = emptyList()

    private val formatoDinero =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-MX")
        )

    class VistaVenta(vista: View) :
        RecyclerView.ViewHolder(vista) {

        val txtFolio: TextView =
            vista.findViewById(R.id.txtFolioVenta)

        val txtOrigen: TextView =
            vista.findViewById(R.id.txtOrigenVenta)

        val txtTotal: TextView =
            vista.findViewById(R.id.txtTotalVenta)

        val txtCliente: TextView =
            vista.findViewById(R.id.txtClienteVenta)

        val txtPuntos: TextView =
            vista.findViewById(R.id.txtPuntosVenta)

        val txtFecha: TextView =
            vista.findViewById(R.id.txtFechaVenta)

        val txtEstado: TextView =
            vista.findViewById(R.id.txtEstadoVenta)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VistaVenta {

        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_venta_cajero,
                parent,
                false
            )

        return VistaVenta(vista)
    }

    override fun onBindViewHolder(
        holder: VistaVenta,
        position: Int
    ) {

        val venta = listaVentas[position]

        // Folio recibido desde la API
        holder.txtFolio.text = venta.folio

        // Origen de la venta
        val origen = when (
            venta.origen.trim().lowercase(Locale.ROOT)
        ) {
            "caja" -> "Caja"
            "app" -> "App"
            else -> venta.origen
        }

        holder.txtOrigen.text = "· $origen"

        // Importe real
        holder.txtTotal.text =
            formatoDinero.format(venta.total)

        // Cliente
        holder.txtCliente.text =
            venta.cliente?.takeIf {
                it.isNotBlank()
            } ?: "Sin cuenta"

        // Puntos ganados
        if (venta.puntos_ganados > 0) {

            holder.txtPuntos.visibility = View.VISIBLE

            holder.txtPuntos.text =
                " · +${venta.puntos_ganados} pts"

        } else {

            holder.txtPuntos.visibility = View.GONE
        }

        // Hora de la venta
        val hora = Regex(
            "\\b\\d{2}:\\d{2}\\b"
        ).find(venta.fecha)?.value

        holder.txtFecha.text =
            if (hora != null) {
                " · $hora"
            } else {
                ""
            }

        // Estado real de la venta
        when (
            venta.estado.trim().lowercase(Locale.ROOT)
        ) {

            "cobrada", "cobrado", "pagada", "pagado" -> {

                holder.txtEstado.text = "Cobrada"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#E4F2DD",
                    "#5F8B43"
                )
            }

            "cancelada", "cancelado" -> {

                holder.txtEstado.text = "Cancelada"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#FBE2DF",
                    "#B24C43"
                )
            }

            "pendiente" -> {

                holder.txtEstado.text = "Pendiente"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#FFE7D5",
                    "#C35B11"
                )
            }

            else -> {

                holder.txtEstado.text = venta.estado

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#EFEAE3",
                    "#75685E"
                )
            }
        }

        // Abrir resumen de la venta
        holder.itemView.setOnClickListener {
            alSeleccionarVenta(venta)
        }
    }

    private fun aplicarEtiqueta(
        texto: TextView,
        colorFondo: String,
        colorTexto: String
    ) {

        val radio =
            15f * texto.resources.displayMetrics.density

        val fondo = GradientDrawable().apply {

            shape = GradientDrawable.RECTANGLE
            cornerRadius = radio

            setColor(
                Color.parseColor(colorFondo)
            )
        }

        texto.background = fondo

        texto.setTextColor(
            Color.parseColor(colorTexto)
        )
    }

    override fun getItemCount(): Int {
        return listaVentas.size
    }

    // Actualizar historial con la respuesta de Retrofit
    fun actualizarVentas(
        nuevasVentas: List<dataVentaCajero>
    ) {

        listaVentas = nuevasVentas
        notifyDataSetChanged()
    }
}