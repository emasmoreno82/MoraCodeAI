package com.example.moracodeai

// Data class: Es un molde puro que solo guarda información.
data class Message(
    val text: String,
    val isFromMora: Boolean // true si lo envió la IA, false si lo enviaste tú
)