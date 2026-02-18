package com.app.hnconverter

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        // Recibir datos del Intent
        val monto = intent.getDoubleExtra("MONTO_ORIGEN", 0.0)
        val moneda = intent.getStringExtra("MONEDA_ORIGEN")
        val resultado = intent.getDoubleExtra("RESULTADO", 0.0)
        val tasa = intent.getDoubleExtra("TASA_APLICADA", 0.0)

        // Referencias del layout (Asegúrate de que los IDs coincidan en tu XML)
        val tvDetalle = findViewById<TextView>(R.id.tvResultadoDetalle)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        // Mostrar el desglose
        tvDetalle.text = """
            Monto: $monto $moneda
            Tasa aplicada: $tasa
            
            TOTAL: ${String.format("%.2f", resultado)} USD
        """.trimIndent()

        // Botón para volver a la principal

    }
}