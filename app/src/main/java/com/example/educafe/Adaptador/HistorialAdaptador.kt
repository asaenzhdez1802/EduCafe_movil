package com.example.educafe.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataHistorial
import com.example.educafe.R


class HistorialAdaptador(
    private var historial: List<dataHistorial>
) : RecyclerView.Adapter<HistorialAdaptador.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTituloPedido: TextView = itemView.findViewById(R.id.txtTituloPedido)
        val txtInsigniaPago: TextView = itemView.findViewById(R.id.txtInsigniaPago)
        val txtInsigniaEstado: TextView = itemView.findViewById(R.id.txtInsigniaEstado)
        val txtLineaFecha: TextView = itemView.findViewById(R.id.txtLineaFecha)
        val txtLineaTotal: TextView = itemView.findViewById(R.id.txtLineaTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido_cliente, parent, false)
        return HistorialViewHolder(vista)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historial[position]
        val esCaja = item.origen == "caja"
        val cancelado = item.estado == "cancelado"

        holder.txtTituloPedido.text = if (esCaja) "Compra ${item.folio ?: ""}" else "Pedido ${item.folio ?: ""}"

        when {
            esCaja -> {
                clsInsignias.pago(holder.txtInsigniaPago, "pagado")
                clsInsignias.estado(holder.txtInsigniaEstado, "entregado")
            }
            cancelado -> {
                clsInsignias.estado(holder.txtInsigniaEstado, "cancelado")
                clsInsignias.pago(holder.txtInsigniaPago, "sin_cobro")
            }
            else -> {
                clsInsignias.pago(holder.txtInsigniaPago, item.estadoPago)
                clsInsignias.estado(holder.txtInsigniaEstado, item.estado)
            }
        }

        var fecha = clsFormato.dia(item.fecha)
        if (clsFormato.esReciente(item.fecha)) {
            fecha = clsFormato.diaYHora(item.fecha)
        }
        val motivo = clsFormato.motivoCancelacion(item.motivoCancelacion)
        if (cancelado) {
            if (motivo != null) fecha += " · $motivo"
        } else if (item.puntosGanados > 0) {
            fecha += " · +${item.puntosGanados} puntos"
        }
        holder.txtLineaFecha.text = fecha

        val total = "Total: ${clsFormato.dinero(item.total)}"
        holder.txtLineaTotal.text = if (cancelado) "$total · sin cargo" else total
    }

    override fun getItemCount(): Int {
        return historial.size
    }

    fun actualizarHistorial(nuevoHistorial: List<dataHistorial>) {
        historial = nuevoHistorial
        notifyDataSetChanged()
    }
}