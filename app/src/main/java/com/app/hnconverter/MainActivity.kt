package com.app.hnconverter

import DatabaseHelper
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        //Referencias de los componentes
        val etAmount = findViewById<EditText>(R.id.editTextNumberDecimal)
        val spinnerFrom = findViewById<Spinner>(R.id.spinner)
        val spinnerTo = findViewById<Spinner>(R.id.spinner3) // El segundo spinner para USD
        val btnConvert = findViewById<Button>(R.id.btnConvertir)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnHistory = findViewById<Button>(R.id.btnHistory)

        //Llenar el primer Spinner (Centroamerica)
        val monedasCA = arrayOf("HNL", "GTQ", "CRC", "NIO", "SVC")
        val adapterCA = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedasCA)
        adapterCA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapterCA

        // 3 Llenar el segundo Spinner (USD)
        val monedaDestino = arrayOf("USD")
        val adapterUSD = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedaDestino)
        adapterUSD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTo.adapter = adapterUSD

        //Configurar eventos de botones
        btnConvert.setOnClickListener {
            realizarConversion(etAmount, spinnerFrom, tvResult)
        }

        btnHistory.setOnClickListener {
            Toast.makeText(this, "Abriendo historial...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarConversion(et: EditText, spinner: Spinner, tv: TextView) {
        val montoStr = et.text.toString()
        if (montoStr.isEmpty()) {
            et.error = "Escribe un monto"
            return
        }

        val monto = montoStr.toDouble()
        val moneda = spinner.selectedItem.toString()

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT rate FROM rates WHERE from_code = ?", arrayOf(moneda))

        if (cursor.moveToFirst()) {
            val tasa = cursor.getDouble(0)
            val resultado = monto * tasa

            // Mostrar resultado en pantalla
            tv.text = String.format("%.2f USD", resultado)

            // Guardar en SQLite
            guardarEnHistorial(moneda, "USD", monto, resultado)
            Toast.makeText(this, "Conversión guardada en SQLite", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error: Tasa no encontrada en la BD", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    private fun guardarEnHistorial(from: String, to: String, amount: Double, result: Double) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("from_code", from)
            put("to_code", to)
            put("amount", amount)
            put("result", result)
            put("date", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
        }
        db.insert("conversions", null, values)
    }
}