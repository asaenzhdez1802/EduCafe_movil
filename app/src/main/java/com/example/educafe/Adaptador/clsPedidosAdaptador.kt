package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Modelo.dataPedidos

class clsPedidosAdaptador(
    private var listaPedidos: List<dataPedidos>
) : RecyclerView.Adapter<clsPedidosAdaptador.VistaPedido>() {

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VistaPedido {

        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_personal, parent, false)

        return VistaPedido(vista)
    }

    override fun onBindViewHolder(
        holder: VistaPedido,
        position: Int
    ) {
        val pedido = listaPedidos[position]

        holder.txtFolio.text = pedido.folio

        holder.txtTotal.text =
            String.format("$%.2f", pedido.total)

        holder.txtInfo.text =
            "${pedido.codigo_recogida} · ${pedido.cliente} · ${pedido.fecha.substring(11, 16)}"

        holder.txtEstadoPago.text = when (pedido.estado_pago) {
            "por_cobrar" -> "Por cobrar"
            "pagado" -> "Pagado"
            "sin_cobro" -> "Sin cobro"
            else -> pedido.estado_pago
        }

        holder.txtEstado.text = when (pedido.estado) {
            "pendiente" -> "Pendiente"
            "listo" -> "Listo"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> pedido.estado
        }
    }

    override fun getItemCount(): Int {
        return listaPedidos.size
    }

    fun actualizarPedidos(nuevosPedidos: List<dataPedidos>) {
        listaPedidos = nuevosPedidos
        notifyDataSetChanged()
    }
}