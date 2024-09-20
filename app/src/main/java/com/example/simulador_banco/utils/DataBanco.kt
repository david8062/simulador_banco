package com.example.simulador_banco.utils

import android.content.Context
import com.example.simulador_banco.login.vm.LoginVm
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class DataBanco {

    // Función para crear un archivo JSON
    fun createJsonFile(context: Context, filename: String) {
        try {
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()

            jsonObject.put("id", "1")
            jsonObject.put("usuario", "Juan")
            jsonObject.put("contrasena", "1234")
            jsonObject.put("correo", "jeffersondavila1101@gmail.com")
            jsonObject.put("telefono", "303579628")
            jsonObject.put("documento", "12345678")
            jsonObject.put("saldo", 1000000)
            jsonObject.put("saldoTotal", 1000000)


            jsonArray.put(jsonObject)

            val file = File(context.filesDir, "$filename.json")
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun createJsonBanco(context: Context, filename: String) {
        try {
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("idIsuario", "1")
            jsonObject.put("usuario", "Juan")
            jsonObject.put("tipoTransaccion", "transferencia")
            jsonObject.put("fechaTransaccion", "23-09-2024")
            jsonObject.put("cantidadTransaccion", "0")
            jsonArray.put(jsonObject)
            val file = File(context.filesDir, "$filename.json")
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Función para leer un archivo JSON
    fun readJsonFile(context: Context, filename: String): String? {
        return try {
            val file = File(context.filesDir, "$filename.json")
            if (file.exists()) {
                file.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateUniqueId(): String {
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        return "$uuid-$timestamp"
    }
}
