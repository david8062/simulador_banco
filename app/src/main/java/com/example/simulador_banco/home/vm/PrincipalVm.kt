package com.example.simulador_banco.home.vm

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simulador_banco.home.model.DataTransferencia
import com.example.simulador_banco.register.model.User
import com.example.simulador_banco.utils.DataBanco
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date

class PrincipalVm(application: Application) : AndroidViewModel(application) {

    val saldoDisponible = MutableLiveData<String>()

    fun actualizarSaldos(saldoDisponible: String) {
        this.saldoDisponible.value = saldoDisponible
    }
    fun obtenerSaldosPorUsuario(jsonData: String, userId: String): Pair<Int, Int>? {
        return try {
            val jsonArray = JSONArray(jsonData)
            for (i in 0 until jsonArray.length()) {
                val usuarioJson = jsonArray.getJSONObject(i)
                if (usuarioJson.getString("id") == userId) {
                    val saldo = usuarioJson.getInt("saldo")
                    val saldoTotal = usuarioJson.getInt("saldo")
                    return Pair(saldo, saldoTotal)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun transferir(context: Context, jsonData: String, userId: String, cantidad: String, numeroDocumento: String) {
        try {
            val cantidadTransferir = cantidad.toInt()
            if (cantidadTransferir <= 0) {
                Toast.makeText(context, "La cantidad a transferir debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val jsonArray = JSONArray(jsonData)
            var saldoRemitente = 0
            var usuarioEncontrado = false
            var nombreRemitente = ""
            var nombreDestinatario = ""
            val updatedJsonArray = JSONArray()

            // Buscar y actualizar el saldo del remitente y del destinatario
            for (i in 0 until jsonArray.length()) {
                val usuarioJson = jsonArray.getJSONObject(i)

                // Si es el remitente
                if (usuarioJson.getString("id") == userId) {
                    saldoRemitente = usuarioJson.getInt("saldo")
                    nombreRemitente = usuarioJson.getString("usuario") // Obtener nombre del remitente
                    if (cantidadTransferir > saldoRemitente) {
                        Toast.makeText(context, "No tienes saldo suficiente", Toast.LENGTH_SHORT).show()
                        return
                    }
                    usuarioJson.put("saldo", saldoRemitente - cantidadTransferir)
                }

                // Si es el destinatario
                if (usuarioJson.getString("documento") == numeroDocumento) {
                    usuarioEncontrado = true
                    nombreDestinatario = usuarioJson.getString("usuario") // Obtener nombre del destinatario
                    usuarioJson.put("saldo", usuarioJson.getInt("saldo") + cantidadTransferir)
                }

                updatedJsonArray.put(usuarioJson)
            }

            if (!usuarioEncontrado) {
                Toast.makeText(context, "Usuario con número de documento $numeroDocumento no encontrado.", Toast.LENGTH_SHORT).show()
                return
            }


            val file = File(context.filesDir, "usuarios.json")
            file.writeText(updatedJsonArray.toString())

            addMovimiento(
                context = context,
                userId = userId,
                tipoTransaccion = "transferencia",
                usuario = nombreRemitente,
                cantidadTransaccion = cantidadTransferir * -1
            )
            addMovimiento(
                context = context,
                userId = numeroDocumento,
                tipoTransaccion = "recepción",
                usuario = nombreDestinatario,
                cantidadTransaccion = cantidadTransferir
            )
            Toast.makeText(context, "Transferencia exitosa.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al realizar la transferencia.", Toast.LENGTH_SHORT).show()
        }
    }


    fun retirar(
        context: Context,
        jsonData: String,
        userId: String,
        cantidad: String
    ) {
        try {
            val cantidadRetirar = cantidad.toInt()
            if (cantidadRetirar <= 0) {
                Toast.makeText(context, "La cantidad a retirar debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val jsonArray = JSONArray(jsonData)
            var usuarioEncontrado = false
            var saldoDisponible = 0
            var nombreUsuario = ""
            val updatedJsonArray = JSONArray()

            for (i in 0 until jsonArray.length()) {
                val usuarioJson = jsonArray.getJSONObject(i)
                if (usuarioJson.getString("id") == userId) {
                    usuarioEncontrado = true
                    saldoDisponible = usuarioJson.getInt("saldo")
                    nombreUsuario = usuarioJson.getString("usuario")

                    if (cantidadRetirar > saldoDisponible) {
                        Toast.makeText(context, "No tienes saldo suficiente", Toast.LENGTH_SHORT).show()
                        return
                    }

                    usuarioJson.put("saldo", saldoDisponible - cantidadRetirar)
                    Toast.makeText(context, "Has retirado $cantidadRetirar", Toast.LENGTH_SHORT).show()
                }
                updatedJsonArray.put(usuarioJson)
            }

            if (!usuarioEncontrado) {
                Toast.makeText(context, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                return
            }


            val file = File(context.filesDir, "usuarios.json")
            file.writeText(updatedJsonArray.toString())

            addMovimiento(
                context = context,
                userId = userId,
                tipoTransaccion = "retiro",
                usuario = nombreUsuario,
                cantidadTransaccion = -cantidadRetirar
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al realizar el retiro.", Toast.LENGTH_SHORT).show()
        }
    }


    fun recarga(
        context: Context,
        jsonData: String,
        userId: String,
        cantidad: String
    ) {
        try {
            val cantidadRecargar = cantidad.toInt()
            if (cantidadRecargar <= 0) {
                Toast.makeText(context, "La cantidad a recargar debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val jsonArray = JSONArray(jsonData)
            var usuarioEncontrado = false
            var saldoDisponible = 0
            var nombreUsuario = ""
            val updatedJsonArray = JSONArray()

            for (i in 0 until jsonArray.length()) {
                val usuarioJson = jsonArray.getJSONObject(i)
                if (usuarioJson.getString("id") == userId) {
                    usuarioEncontrado = true
                    saldoDisponible = usuarioJson.getInt("saldo")
                    nombreUsuario = usuarioJson.getString("usuario")

                    usuarioJson.put("saldo", saldoDisponible + cantidadRecargar)
                    Toast.makeText(context, "Has recargado $cantidadRecargar", Toast.LENGTH_SHORT).show()
                }
                updatedJsonArray.put(usuarioJson)
            }

            if (!usuarioEncontrado) {
                Toast.makeText(context, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                return
            }

            // Guardar el nuevo estado del archivo 'usuarios.json'
            val file = File(context.filesDir, "usuarios.json")
            file.writeText(updatedJsonArray.toString())

            addMovimiento(
                context = context,
                userId = userId,
                tipoTransaccion = "recarga",
                usuario = nombreUsuario,
                cantidadTransaccion = cantidadRecargar // Es una entrada de dinero
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al realizar la recarga.", Toast.LENGTH_SHORT).show()
        }
    }



    fun addMovimiento(
        context: Context,
        userId: String,
        tipoTransaccion: String,
        usuario: String,
        cantidadTransaccion: Int
    ) {
        viewModelScope.launch {
            val file = File(context.filesDir, "dataMovimientos.json")
            val jsonArray = if (file.exists()) {
                try {
                    JSONArray(file.readText())
                } catch (e: Exception) {
                    JSONArray().apply {
                        put(JSONObject(file.readText()))
                    }
                }
            } else {
                JSONArray()
            }

            // Crear el objeto de transferencia
            val dataTransferencia = DataTransferencia(
                idIsuario = userId,
                usuario = usuario,
                tipoTransaccion = tipoTransaccion,
                fechaTransaccion = Date(),
                cantidadTransaccion = cantidadTransaccion
            )

            // Convertir el objeto DataTransferencia a JSONObject
            val jsonObject = JSONObject().apply {
                put("idIsuario", dataTransferencia.idIsuario)
                put("usuario", dataTransferencia.usuario)
                put("tipoTransaccion", dataTransferencia.tipoTransaccion)
                put("fechaTransaccion", dataTransferencia.fechaTransaccion.toString())
                put("cantidadTransaccion", dataTransferencia.cantidadTransaccion)
            }

            jsonArray.put(jsonObject)


            try {
                file.writeText(jsonArray.toString())
                Toast.makeText(context, "Movimiento registrado exitosamente", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al registrar el movimiento", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }




}

