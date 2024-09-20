package com.example.simulador_banco.login.vm

import androidx.lifecycle.ViewModel
import android.content.Context
import com.example.simulador_banco.utils.DataBanco
import org.json.JSONArray

class LoginVm : ViewModel() {

    fun validarCredenciales(context: Context, usuarioInput: String, contrasenaInput: String): String? {
        val dataBanco = DataBanco()
        val jsonData = dataBanco.readJsonFile(context, "usuarios")
        jsonData?.let {
            val jsonArray = JSONArray(it)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val usuarioJson = jsonObject.getString("usuario")
                val contrasenaJson = jsonObject.getString("contrasena")

                if (usuarioInput == usuarioJson && contrasenaInput == contrasenaJson) {
                    return jsonObject.getString("id")
                }
            }
        }
        return null
    }

}
