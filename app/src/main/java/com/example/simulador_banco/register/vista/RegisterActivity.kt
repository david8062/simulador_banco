package com.example.simulador_banco.register.vista

import RegisterViewModel
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simulador_banco.R
import com.example.simulador_banco.login.vista.LoginActivity
import com.example.simulador_banco.register.model.User
import com.example.simulador_banco.utils.DataBanco

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private val dataBanco = DataBanco()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //variables

        val usuario = findViewById<EditText>(R.id.txtUsuario)
        val contrasena = findViewById<EditText>(R.id.txtContrasenaRegistro)
        val documento = findViewById<EditText>(R.id.txtDocumento)
        val correo = findViewById<EditText>(R.id.txtEmail)
        val telefono = findViewById<EditText>(R.id.txtTelefonoRegistro)
        val botonRegistro = findViewById<Button>(R.id.botonRegistro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        botonRegistro.setOnClickListener {
            val user = User(
                id = dataBanco.generateUniqueId(),
                usuario = usuario.text.toString(),
                contrasena = contrasena.text.toString(),
                correo = correo.text.toString(),
                telefono = telefono.text.toString(),
                documento = documento.text.toString(),
                saldo = 0,
                saldoTotal = 0
            )
            registerViewModel.addUser(user)

            Toast.makeText(this, "Registro exitoso. Redirigiendo al login...", Toast.LENGTH_SHORT).show()

            // Puede ser útil asegurarse de que los datos se hayan agregado antes de iniciar la nueva actividad
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }

    }
}
