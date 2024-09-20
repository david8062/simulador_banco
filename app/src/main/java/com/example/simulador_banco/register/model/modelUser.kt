package com.example.simulador_banco.register.model
data class User(
    val id: String,
    val usuario: String,
    val contrasena: String,
    val correo: String,
    val telefono: String,
    val documento: String,
    val saldo: Int ,
    val saldoTotal: Int
)
