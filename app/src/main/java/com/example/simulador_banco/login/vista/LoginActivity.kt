package com.example.simulador_banco.login.vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simulador_banco.R
import com.example.simulador_banco.home.vista.PrincipalScreenActivity
import com.example.simulador_banco.login.vm.LoginVm
import com.example.simulador_banco.utils.DataBanco
import java.io.File

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginVm by viewModels()
    fun archivoJsonExiste(context: Context, filename: String): Boolean {
        val file = File(context.filesDir, "$filename.json")
        Log.d("DataBanco", "Archivo JSON Se encuentra en ${file}")
        return file.exists()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)


        //variables

        val botonEntrarLogin = findViewById<Button>(R.id.btn_entrar_login)
        val usuario = findViewById<EditText>(R.id.txtCorreo)
        val contrasena = findViewById<EditText>(R.id.txtContrasena)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //funciones
        botonEntrarLogin.setOnClickListener {
            val usuarioInput = usuario.text.toString()
            val contrasenaInput = contrasena.text.toString()

            val usuarioId = loginViewModel.validarCredenciales(this, usuarioInput, contrasenaInput)

            if (usuarioId != null) {
                // Guardar el ID del usuario en SharedPreferences
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("id", usuarioId)
                editor.apply()
                Log.d("tag", "El valor del id es : $sharedPreferences")

                // Iniciar la actividad principal
                val intent = Intent(this, PrincipalScreenActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
}
}