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

// Adaptador compartido por los módulos de Cajero y Administrador.
class clsPedidosAdaptador(
    private var listaPedidos: List<dataPedidos>,
    private val alSeleccionarPedido: (Int) -> Unit = {}
) : RecyclerView.Adapter<clsPedidosAdaptador.VistaPedido>() {

    private val formatoDinero = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))

    // Estilo visual de cada etiqueta.
    private data class EstiloEtiqueta(val texto: String, val colorFondo: String, val colorTexto: String)
    // Referencias del XML de cada pedido.
    class VistaPedido(vista: View) : RecyclerView.ViewHolder(vista) {
        val txtFolio: TextView = vista.findViewById(R.id.txtFolio)
        val txtEstadoPago: TextView = vista.findViewById(R.id.txtEstadoPago)
        val txtTotal: TextView = vista.findViewById(R.id.txtTotal)
        val txtInfo: TextView = vista.findViewById(R.id.txtInfo)
        val txtEstado: TextView = vista.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaPedido {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_personal, parent, false)
        return VistaPedido(vista)
    }

    override fun onBindViewHolder(holder: VistaPedido, position: Int) {
        val pedido = listaPedidos[position]

        holder.txtFolio.text = pedido.folio
        holder.txtTotal.text = formatoDinero.format(pedido.total)

        val hora = Regex("\\b\\d{2}:\\d{2}\\b").find(pedido.fecha)?.value.orEmpty()

        holder.txtInfo.text = listOf(pedido.codigo_recogida, pedido.cliente, hora).filter { it.isNotBlank() }.joinToString(" · ")

        // ETIQUETA SUPERIOR: clasificación de pago según el estado real
        // y, cuando está cancelado, según el motivo de cancelación.
        val estiloPago = obtenerEstiloPago(pedido)
        aplicarEtiqueta(holder.txtEstadoPago, estiloPago.colorFondo, estiloPago.colorTexto)
        holder.txtEstadoPago.text = estiloPago.texto

        // ETIQUETA INFERIOR: conserva el estado real del pedido.
        val estiloEstado = obtenerEstiloEstado(pedido.estado)
        aplicarEtiqueta(holder.txtEstado, estiloEstado.colorFondo, estiloEstado.colorTexto)
        holder.txtEstado.text = estiloEstado.texto

        holder.itemView.setOnClickListener { alSeleccionarPedido(pedido.id_pedido) }
    }

    private fun obtenerEstiloPago(pedido: dataPedidos): EstiloEtiqueta {
        val estado = pedido.estado.trim().lowercase(Locale.ROOT)
        val motivo = pedido.motivo_cancelacion
            ?.trim()
            ?.lowercase(Locale.ROOT)
        val estadoPago = pedido.estado_pago
            .trim()
            .lowercase(Locale.ROOT)
            .replace(' ', '_')

        return when {
            estado == "pendiente" -> EstiloEtiqueta("No listo", "#FFE7D5", "#C35B11")
            estado == "listo" -> EstiloEtiqueta("Por cobrar", "#FFF1CB", "#946E22")
            estado == "entregado" -> EstiloEtiqueta("Pagado", "#D9F1E9", "#287260")
            estado == "cancelado" && motivo == "vencido" -> EstiloEtiqueta("Sin cobro", "#EFEAE3", "#75685E")
            estadoPago == "sin_cobro" -> EstiloEtiqueta("Sin cobro", "#EFEAE3", "#75685E")
            estadoPago == "por_cobrar" -> EstiloEtiqueta("Por cobrar", "#FFF1CB", "#946E22")
            estadoPago == "pagado" -> EstiloEtiqueta("Pagado", "#D9F1E9", "#287260")
            else -> EstiloEtiqueta(pedido.estado_pago.ifBlank { "Sin dato" }, "#EFEAE3", "#75685E")
        }
    }

    private fun obtenerEstiloEstado(estadoOriginal: String): EstiloEtiqueta {
        return when (estadoOriginal.trim().lowercase(Locale.ROOT)) {
            "pendiente" -> EstiloEtiqueta("Pendiente", "#FFE7D5", "#C35B11")
            "listo" -> EstiloEtiqueta("Listo", "#E4F2DD", "#5F8B43")
            "entregado" -> EstiloEtiqueta("Entregado", "#F3EAE1", "#77675A")
            "cancelado" -> EstiloEtiqueta("Cancelado", "#FBE2DF", "#B24C43")
            else -> EstiloEtiqueta(estadoOriginal, "#EFEAE3", "#75685E")
        }
    }

    private fun aplicarEtiqueta(texto: TextView, colorFondo: String, colorTexto: String) {
        val radio = 14f * texto.resources.displayMetrics.density
        val fondo = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radio
            setColor(Color.parseColor(colorFondo))
        }
        texto.background = fondo
        texto.setTextColor(Color.parseColor(colorTexto))
    }

    override fun getItemCount(): Int = listaPedidos.size

    fun actualizarPedidos(nuevosPedidos: List<dataPedidos>) {
        listaPedidos = nuevosPedidos
        notifyDataSetChanged()
    }
}