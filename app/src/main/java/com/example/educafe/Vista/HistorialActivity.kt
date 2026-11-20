package com.example.educafe.Vista

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.HistorialAdaptador
import com.example.educafe.Modelo.dataHistorial
import com.example.educafe.Presentador.clsHistorialPresentador
import com.example.educafe.R


class HistorialActivity : AppCompatActivity() {

    private lateinit var btnAtras: FrameLayout
    private lateinit var txtChipActivos: TextView
    private lateinit var rvHistorial: RecyclerView
    private lateinit var presentador: clsHistorialPresentador
    private lateinit var adaptador: HistorialAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAtras = findViewById(R.id.btnAtras)
        txtChipActivos = findViewById(R.id.txtChipActivos)
        rvHistorial = findViewById(R.id.rvHistorial)

        presentador = clsHistorialPresentador(this)
        rvHistorial.layoutManager = LinearLayoutManager(this)
        adaptador = HistorialAdaptador(emptyList())
        rvHistorial.adapter = adaptador

        btnAtras.setOnClickListener { finish() }
        txtChipActivos.setOnClickListener { finish() }

        NavegacionCliente.configurarBarra(this, NavegacionCliente.PEDIDOS)
    }

    override fun onResume() {
        super.onResume()
        presentador.cargar()
    }

    override fun onDestroy() {
        presentador.destruir()
        super.onDestroy()
    }


    fun mostrarHistorial(historial: List<dataHistorial>) {
        adaptador.actualizarHistorial(historial)
    }

    fun sesionVencida() {
        NavegacionCliente.sesionVencida(this)
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
