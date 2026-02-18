package com.app.hnconverter
import DatabaseHelper
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)

        // 1. Referencias
        val etMonto = findViewById<EditText>(R.id.etMonto)
        val spOrigen = findViewById<Spinner>(R.id.spOrigen)
        val spinnerTo = findViewById<Spinner>(R.id.spUSD)
        val btnConvertir = findViewById<Button>(R.id.btnConvertir)
        val btnFavoritos = findViewById<Button>(R.id.btnFavoritos)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnConfigurar = findViewById<Button>(R.id.btnConfigurar)

        //LLenar Spinners
        val monedasCA = arrayOf("HNL", "GTQ", "CRC", "NIO", "SVC")
        val adapterCA = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedasCA)
        adapterCA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOrigen.adapter = adapterCA

        val monedaDestino = arrayOf("USD")
        val adapterUSD = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedaDestino)
        adapterUSD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTo.adapter = adapterUSD

        //Eventos de los botones
        btnConvertir.setOnClickListener {
            realizarConversion(etMonto, spOrigen)
        }
        btnHistorial.setOnClickListener {
            Toast.makeText(this, "Abriendo historial...", Toast.LENGTH_SHORT).show()
        }
        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Abriendo favoritos...", Toast.LENGTH_SHORT).show()
        }
        btnConfigurar.setOnClickListener {
            Toast.makeText(this, "Abriendo configuración...", Toast.LENGTH_SHORT).show()
        }
    }

 
    private fun realizarConversion(et: EditText, spinner: Spinner) {
        val montoStr = et.text.toString()
        if (montoStr.isEmpty()) {
            et.error = "Escribe un monto"
            return
        }
        val monto = montoStr.toDouble()
        val monedaOrigen = spinner.selectedItem.toString()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT rate FROM rates WHERE from_code = ?", arrayOf(monedaOrigen))

        if (cursor.moveToFirst()) {
            val tasa = cursor.getDouble(0)
            val resultado = monto * tasa
            guardarEnHistorial(monedaOrigen, "USD", monto, resultado)

            val intent = Intent(this, ResultadoActivity::class.java).apply {
                putExtra("MONTO_ORIGEN", monto)
                putExtra("MONEDA_ORIGEN", monedaOrigen)
                putExtra("RESULTADO", resultado)
                putExtra("TASA_APLICADA", tasa)
                putExtra("MONEDA_DESTINO", "USD")
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: Tasa no encontrada", Toast.LENGTH_SHORT).show()
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