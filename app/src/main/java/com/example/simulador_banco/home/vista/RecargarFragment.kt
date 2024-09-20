package com.example.simulador_banco.home.vista

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.simulador_banco.R
import com.example.simulador_banco.home.vm.PrincipalVm
import com.example.simulador_banco.utils.DataBanco


class RecargarFragment : Fragment() {
    private val viewModel: PrincipalVm by activityViewModels()
    private lateinit var dataBanco: DataBanco

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recargar, container, false)
        dataBanco = DataBanco()

        val cantidadRecargar = view.findViewById<EditText>(R.id.txtRecarga)
        val botonRecarga = view.findViewById<Button>(R.id.botonRecargar)
        val saldoDisponible = view.findViewById<TextView>(R.id.txtSaldoDisponibleRecarga)

        viewModel.saldoDisponible.observe(viewLifecycleOwner) { saldo ->
            saldo?.let {
                saldoDisponible.text = it
            }
        }

        botonRecarga.setOnClickListener {
            val cantidad = cantidadRecargar.text.toString()
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("id", null)

            if (userId != null) {
                val jsonData = dataBanco.readJsonFile(requireContext(), "usuarios")
                jsonData?.let {
                    viewModel.recarga(requireContext(), it, userId, cantidad)
                }
            } else {
                Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}