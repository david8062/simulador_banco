import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simulador_banco.register.model.User
import com.example.simulador_banco.utils.DataBanco
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBanco = DataBanco()
    private val context = getApplication<Application>().applicationContext

    fun addUser(user: User) {
        viewModelScope.launch {
            val file = File(context.filesDir, "usuarios.json")
            val jsonArray = if (file.exists()) {
                try {
                    JSONArray(file.readText())
                } catch (e: Exception) {
                    // Si el archivo tiene un JSONObject en lugar de JSONArray, convertirlo a JSONArray
                    JSONArray().apply {
                        put(JSONObject(file.readText()))
                    }
                }

            } else {
                JSONArray()
            }
            val userExists = (0 until jsonArray.length()).any { index ->
                val existingUser = jsonArray.getJSONObject(index)
                existingUser.getString("usuario") == user.usuario
            }

            if (userExists) {
                Toast.makeText(context, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Convertir el modelo User a JSONObject
            val jsonObject = JSONObject().apply {
                put("id", dataBanco.generateUniqueId())
                put("usuario", user.usuario)
                put("contrasena", user.contrasena)
                put("correo", user.correo)
                put("telefono", user.telefono)
                put("documento", user.documento)
                put("saldo", 0)
                put("saldo total", 0)

            }
            jsonArray.put(jsonObject)
            try {
                file.writeText(jsonArray.toString())
                Toast.makeText(context, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Mostrar mensaje de error
                Toast.makeText(context, "Error al guardar el usuario", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}


