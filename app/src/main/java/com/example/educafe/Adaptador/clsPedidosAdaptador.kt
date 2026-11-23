package com.example.educafe.Adaptador

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Modelo.dataPedidos
import java.text.NumberFormat
import java.util.Locale

// Adaptador de pedidos para K2 y A2
class clsPedidosAdaptador(
    private var listaPedidos: List<dataPedidos>,
    private val alSeleccionarPedido: (Int) -> Unit = {}
) : RecyclerView.Adapter<clsPedidosAdaptador.VistaPedido>() {

    // Formato de dinero mexicano
    private val formatoDinero =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-MX")
        )

    // Referencias del XML de cada pedido
    class VistaPedido(vista: View) :
        RecyclerView.ViewHolder(vista) {

        val txtFolio: TextView =
            vista.findViewById(R.id.txtFolio)

        val txtEstadoPago: TextView =
            vista.findViewById(R.id.txtEstadoPago)

        val txtTotal: TextView =
            vista.findViewById(R.id.txtTotal)

        val txtInfo: TextView =
            vista.findViewById(R.id.txtInfo)

        val txtEstado: TextView =
            vista.findViewById(R.id.txtEstado)
    }

    // Crear cada fila usando el diseño XML
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VistaPedido {

        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_pedido_personal,
                parent,
                false
            )

        return VistaPedido(vista)
    }

    // Colocar los datos reales de la API
    override fun onBindViewHolder(
        holder: VistaPedido,
        position: Int
    ) {

        val pedido = listaPedidos[position]

        // Folio
        holder.txtFolio.text = pedido.folio

        // Total del pedido
        holder.txtTotal.text =
            formatoDinero.format(pedido.total)

        // Obtener la hora de la fecha recibida
        val hora = Regex(
            "\\b\\d{2}:\\d{2}\\b"
        ).find(pedido.fecha)?.value.orEmpty()

        // Código de recogida, cliente y hora
        val informacion = listOf(
            pedido.codigo_recogida,
            pedido.cliente,
            hora
        ).filter {
            it.isNotBlank()
        }.joinToString(" · ")

        holder.txtInfo.text = informacion

        // ESTADO DEL PAGO
        when (
            pedido.estado_pago.trim()
                .lowercase(Locale.ROOT)
        ) {

            "por_cobrar" -> {

                holder.txtEstadoPago.text =
                    "Por cobrar"

                aplicarEtiqueta(
                    holder.txtEstadoPago,
                    "#FFF1CB",
                    "#946E22"
                )
            }

            "pagado" -> {

                holder.txtEstadoPago.text =
                    "Pagado"

                aplicarEtiqueta(
                    holder.txtEstadoPago,
                    "#D9F1E9",
                    "#287260"
                )
            }

            "sin_cobro" -> {

                holder.txtEstadoPago.text =
                    "Sin cobro"

                aplicarEtiqueta(
                    holder.txtEstadoPago,
                    "#EFEAE3",
                    "#75685E"
                )
            }

            else -> {

                holder.txtEstadoPago.text =
                    pedido.estado_pago

                aplicarEtiqueta(
                    holder.txtEstadoPago,
                    "#EFEAE3",
                    "#75685E"
                )
            }
        }

        // ESTADO DEL PEDIDO
        when (
            pedido.estado.trim()
                .lowercase(Locale.ROOT)
        ) {

            "pendiente" -> {

                holder.txtEstado.text = "Pendiente"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#FFE7D5",
                    "#C35B11"
                )
            }

            "listo" -> {

                holder.txtEstado.text = "Listo"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#E4F2DD",
                    "#5F8B43"
                )
            }

            "entregado" -> {

                holder.txtEstado.text = "Entregado"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#F3EAE1",
                    "#77675A"
                )
            }

            "cancelado" -> {

                holder.txtEstado.text = "Cancelado"

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#FBE2DF",
                    "#B24C43"
                )
            }

            else -> {

                holder.txtEstado.text = pedido.estado

                aplicarEtiqueta(
                    holder.txtEstado,
                    "#EFEAE3",
                    "#75685E"
                )
            }
        }

        // Abrir K3 al tocar el pedido
        holder.itemView.setOnClickListener {

            alSeleccionarPedido(
                pedido.id_pedido
            )
        }
    }

    // Dar forma redondeada a las etiquetas
    private fun aplicarEtiqueta(
        texto: TextView,
        colorFondo: String,
        colorTexto: String
    ) {

        val radio =
            14f * texto.resources.displayMetrics.density

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

    // Cantidad de pedidos
    override fun getItemCount(): Int {
        return listaPedidos.size
    }

    // Actualizar datos al recibir respuesta de API
    fun actualizarPedidos(
        nuevosPedidos: List<dataPedidos>
    ) {

        listaPedidos = nuevosPedidos

        notifyDataSetChanged()
    }
}