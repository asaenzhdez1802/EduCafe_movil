package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Modelo.dataPedidos

// Adaptador para mostrar los pedidos del cajero
class clsPedidosAdaptador(private var listaPedidos: List<dataPedidos>, private val alSeleccionarPedido: (Int) -> Unit = {}) : RecyclerView.Adapter<clsPedidosAdaptador.VistaPedido>() {

    // Elementos visuales de cada pedido
    class VistaPedido(vista: View) : RecyclerView.ViewHolder(vista) {

        val txtFolio: TextView = vista.findViewById(R.id.txtFolio)
        val txtEstadoPago: TextView = vista.findViewById(R.id.txtEstadoPago)
        val txtTotal: TextView = vista.findViewById(R.id.txtTotal)
        val txtInfo: TextView = vista.findViewById(R.id.txtInfo)

        val txtEstado: TextView = vista.findViewById(R.id.txtEstado)
    }

    // Crear el diseño de cada pedido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaPedido {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_personal, parent, false)
        return VistaPedido(vista)
    }

    // Mostrar la información de cada pedido
    override fun onBindViewHolder(holder: VistaPedido, position: Int) {

        // Obtener el pedido correspondiente
        val pedido = listaPedidos[position]

        // Mostrar folio del pedido
        holder.txtFolio.text = pedido.folio

        // Mostrar total del pedido
        holder.txtTotal.text = String.format(java.util.Locale("es", "MX"),
            "$%.2f", pedido.total)

        // Obtener la hora, evitando errores si la fecha viene incompleta
        val hora = if (pedido.fecha.length >= 16) {
            pedido.fecha.substring(11, 16)
        } else {
            pedido.fecha
        }

        // Mostrar código, cliente y hora
        holder.txtInfo.text = "${pedido.codigo_recogida} · ${pedido.cliente} · $hora"

        // Mostrar estado del pago
        holder.txtEstadoPago.text = when (pedido.estado_pago) {
            "por_cobrar" -> "Por cobrar"
            "pagado" -> "Pagado"
            "sin_cobro" -> "Sin cobro"
            else -> pedido.estado_pago
        }

        // Mostrar estado del pedido
        holder.txtEstado.text = when (pedido.estado) {
            "pendiente" -> "Pendiente"
            "listo" -> "Listo"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> pedido.estado
        }

        // Detectar cuando el cajero selecciona un pedido
        holder.itemView.setOnClickListener {
            // Enviar el ID del pedido seleccionado
            alSeleccionarPedido(pedido.id_pedido)
        }
    }

    // Obtener la cantidad de pedidos
    override fun getItemCount(): Int {
        return listaPedidos.size
    }

    // Actualizar la lista cuando llegan pedidos de la API
    fun actualizarPedidos(nuevosPedidos: List<dataPedidos>) {
        listaPedidos = nuevosPedidos
        notifyDataSetChanged()
    }
}