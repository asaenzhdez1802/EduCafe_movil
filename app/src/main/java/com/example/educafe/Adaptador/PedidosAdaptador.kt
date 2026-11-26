package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataPedidoCliente
import com.example.educafe.R
import com.example.educafe.Vista.MisPedidosActivity


class PedidosAdaptador(
    private var pedidos: List<dataPedidoCliente>,
    private val activity: MisPedidosActivity
) : RecyclerView.Adapter<PedidosAdaptador.PedidoViewHolder>() {

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTituloPedido: TextView = itemView.findViewById(R.id.txtTituloPedido)
        val txtInsigniaPago: TextView = itemView.findViewById(R.id.txtInsigniaPago)
        val txtInsigniaEstado: TextView = itemView.findViewById(R.id.txtInsigniaEstado)
        val txtLineaFecha: TextView = itemView.findViewById(R.id.txtLineaFecha)
        val txtLineaTotal: TextView = itemView.findViewById(R.id.txtLineaTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_cliente, parent, false)
        return PedidoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.txtTituloPedido.text = "Pedido ${pedido.folio ?: pedido.idPedido}"

        if (pedido.estado == "pendiente") {
            // El cajero aún no lo marca: [Pendiente] [No listo]
            clsInsignias.estado(holder.txtInsigniaEstado, "pendiente")
            clsInsignias.noListo(holder.txtInsigniaPago)
        } else {
            // El cajero ya lo marcó: [Listo] [Por cobrar] o [Listo] [Pagado]
            clsInsignias.estado(holder.txtInsigniaEstado, pedido.estado)
            clsInsignias.pago(holder.txtInsigniaPago, pedido.estadoPago)
        }

        holder.txtLineaFecha.text = "${clsFormato.diaYHora(pedido.fecha)} · Código ${pedido.codigoRecogida ?: "-"}"

        val total = "Total: ${clsFormato.dinero(pedido.total, false)}"
        if (pedido.estadoPago == "por_cobrar") {
            holder.txtLineaTotal.text = "$total · paga antes de las ${clsFormato.horaCorta(pedido.fechaVence)}"
        } else {
            holder.txtLineaTotal.text = total
        }

        holder.itemView.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    activity.abrirPedido(pedido)
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    fun actualizarPedidos(nuevosPedidos: List<dataPedidoCliente>) {
        pedidos = nuevosPedidos
        notifyDataSetChanged()
    }
}
