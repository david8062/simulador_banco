package com.example.simulador_banco

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simulador_banco.login.vista.LoginActivity
import com.example.simulador_banco.register.vista.RegisterActivity
import com.example.simulador_banco.utils.DataBanco
import java.io.File

class MainActivity : AppCompatActivity() {
    // Instanciamos la clase
    private lateinit var dataBanco: DataBanco

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dataBanco = DataBanco()

        val botonEntrar = findViewById<Button>(R.id.btn_entrar)
        val botonRegistro = findViewById<Button>(R.id.btn_registrar)

        if (!archivoJsonExiste(this, "usuarios")) {

            dataBanco.createJsonFile(this, "usuarios")
            dataBanco.createJsonBanco(this, "dataMovimientos")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        botonEntrar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        botonRegistro.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun archivoJsonExiste(context: Context, filename: String): Boolean {
        val file = File(context.filesDir, "$filename.json")
        val exists = file.exists()
        if (exists) {
            val jsonString = file.readText()
            Log.d("Archivo JSON", "el contenido del arhivo es: $jsonString")
        } else {
            Log.d("DataBanco", "El archivo JSON no existe.")
        }
        return exists
    }

}
