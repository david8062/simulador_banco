package com.example.simulador_banco.home.vista

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.simulador_banco.R
import com.example.simulador_banco.home.vm.PrincipalVm
import com.example.simulador_banco.utils.DataBanco

class TransferirFragment : Fragment() {
    private val viewModel: PrincipalVm by activityViewModels()
    private lateinit var dataBanco: DataBanco

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transferir, container, false)

        dataBanco = DataBanco()

        val cantidadEditText = view.findViewById<EditText>(R.id.txtTransCantidad)
        val numeroDocumentoEditText = view.findViewById<EditText>(R.id.txtTransNumero)
        val transferirButton = view.findViewById<Button>(R.id.botonTransferir)
        val saldoDisponible = view.findViewById<TextView>(R.id.txtSaldoDisponibleTransferencia)

        viewModel.saldoDisponible.observe(viewLifecycleOwner) { saldo ->
            saldo?.let {
                saldoDisponible.text = it
            }
        }


        transferirButton.setOnClickListener {
            val cantidad = cantidadEditText.text.toString().trim()
            val numeroDocumento = numeroDocumentoEditText.text.toString().trim()

            if (cantidad.isEmpty() || numeroDocumento.isEmpty()) {
                // Mostrar un mensaje al usuario indicando que todos los campos son requeridos
                Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("id", null)

            userId?.let { id ->
                val context = context ?: return@let
                val jsonData = try {
                    dataBanco.readJsonFile(context, "usuarios")
                } catch (e: Exception) {
                    Log.e("TAG", "Error al leer el archivo JSON", e)
                    null
                }

                jsonData?.let { data ->
                    viewModel.transferir(
                        context = requireContext(),
                        jsonData = data,
                        userId = id,
                        cantidad = cantidad,
                        numeroDocumento = numeroDocumento
                    )
                } ?: run {

                    Log.e("TAG", "No se pudo leer el archivo JSON")
                }
            } ?: run {

                Log.e("TAG", "No se pudo obtener el ID del usuario")
            }
        }
        return view
    }
}


