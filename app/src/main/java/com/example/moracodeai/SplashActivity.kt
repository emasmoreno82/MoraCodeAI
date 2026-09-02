package com.example.moracodeai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ocultar la barra superior (ActionBar) si existe, para que se vea a pantalla completa
        supportActionBar?.hide()

        // Temporizador de 5 segundos (5000 milisegundos)
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)

            // Pasados los 5 segundos, abrimos la pantalla principal (MainActivity)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Cerramos esta pantalla para que no se pueda volver atrás al Splash
            finish()
        }
    }
}