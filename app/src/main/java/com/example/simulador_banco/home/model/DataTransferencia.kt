package com.example.simulador_banco.home.model
import java.util.Date
data class DataTransferencia(
    val idIsuario: String,
    val usuario: String,
    val tipoTransaccion: String,
    val fechaTransaccion: Date,
    val cantidadTransaccion: Int
)
