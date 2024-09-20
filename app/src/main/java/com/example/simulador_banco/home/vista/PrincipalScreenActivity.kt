package com.example.simulador_banco.home.vista

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.simulador_banco.R
import com.example.simulador_banco.home.model.DataTransferencia
import com.example.simulador_banco.home.vm.PrincipalVm
import com.example.simulador_banco.utils.DataBanco
import org.json.JSONArray
import java.io.File
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrincipalScreenActivity : AppCompatActivity() {

    private val dataBanco = DataBanco()
    private lateinit var listView: ListView
    private lateinit var adapter: MovimientosAdapter
    private val movimientos = mutableListOf<DataTransferencia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_screen)

        val txtSaldoDisponible = findViewById<TextView>(R.id.txtSaldoNumero)
        val txtSaldoTotal = findViewById<TextView>(R.id.txtSaldoTotal)
        val botonTransferir = findViewById<Button>(R.id.btn_transferir)
        val botonCargar = findViewById<Button>(R.id.btn_Recargar)
        val botonRetirar = findViewById<Button>(R.id.btn_retirar)
        listView = findViewById(R.id.listViewMovimientos)
        adapter = MovimientosAdapter(this, movimientos)
        listView.adapter = adapter

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", null)
        cargarMovimientos(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principalScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        botonTransferir.setOnClickListener {
            val fragment = TransferirFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        botonRetirar.setOnClickListener {
            val fragment = RetiroFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        botonCargar.setOnClickListener {
            val fragment = RecargarFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val viewModel = ViewModelProvider(this).get(PrincipalVm::class.java)

        userId?.let { id ->
            val jsonData = dataBanco.readJsonFile(this, "usuarios")
            jsonData?.let {
                val saldos = viewModel.obtenerSaldosPorUsuario(jsonData, id)
                saldos?.let { (saldoDisponible, saldoTotal) ->
                    txtSaldoDisponible.text = "$saldoDisponible"
                    txtSaldoTotal.text = "$saldoTotal"
                    viewModel.actualizarSaldos(saldoDisponible.toString())
                }
            }
        }
    }

    private fun cargarMovimientos(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", null)

        if (userId == null) {
            Toast.makeText(context, "Error: Usuario no logueado.", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(context.filesDir, "dataMovimientos.json")
        if (!file.exists()) {
            Toast.makeText(context, "No hay movimientos registrados.", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonArray = JSONArray(file.readText())
        movimientos.clear()

        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val idUsuario = if (jsonObject.has("idIsuario")) jsonObject.getString("idIsuario") else "Desconocido"
            val usuario = if (jsonObject.has("usuario")) jsonObject.getString("usuario") else "Desconocido"
            val tipoTransaccion = if (jsonObject.has("tipoTransaccion")) jsonObject.getString("tipoTransaccion") else "Desconocido"
            val fechaTransaccionString = if (jsonObject.has("fechaTransaccion")) jsonObject.getString("fechaTransaccion") else "1970-01-01"
            val cantidadTransaccion = if (jsonObject.has("cantidadTransaccion")) jsonObject.getInt("cantidadTransaccion") else 0

            val fechaTransaccion = try {
                dateFormat.parse(fechaTransaccionString) ?: Date()
            } catch (e: Exception) {
                Date()
            }

            // Filtrar movimientos por usuario logueado
            if (idUsuario == userId) {
                val movimiento = DataTransferencia(
                    idIsuario = idUsuario,
                    usuario = usuario,
                    tipoTransaccion = tipoTransaccion,
                    fechaTransaccion = fechaTransaccion,
                    cantidadTransaccion = cantidadTransaccion
                )

                movimientos.add(movimiento)
            }
        }

        if (movimientos.isEmpty()) {
            Toast.makeText(context, "No hay movimientos para este usuario.", Toast.LENGTH_SHORT).show()
        } else {
            val listView = findViewById<ListView>(R.id.listViewMovimientos)
            val adapter = MovimientosAdapter(context, movimientos)
            listView.adapter = adapter
        }
    }


}

class MovimientosAdapter(context: Context, private val movimientos: List<DataTransferencia>) :
    ArrayAdapter<DataTransferencia>(context, 0, movimientos) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false)
        val movimiento = getItem(position)

        val txtUsuario = view.findViewById<TextView>(R.id.txtUsuario)
        val txtTipoTransaccion = view.findViewById<TextView>(R.id.txtTipoTransaccion)
        val txtFechaTransaccion = view.findViewById<TextView>(R.id.txtFecha)
        val txtCantidadTransaccion = view.findViewById<TextView>(R.id.txtCantidad)

        txtUsuario.text = movimiento?.usuario
        txtTipoTransaccion.text = movimiento?.tipoTransaccion
        txtFechaTransaccion.text = dateFormat.format(movimiento?.fechaTransaccion)
        txtCantidadTransaccion.text = movimiento?.cantidadTransaccion.toString()

        return view
    }
}
