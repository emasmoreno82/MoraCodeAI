package com.example.moracodeai

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var editCode: EditText
    private lateinit var btnRunCode: Button
    private lateinit var btnCheckCode: Button
    private lateinit var btnChallenge: Button
    private lateinit var btnDetail: Button
    private lateinit var btnToggleVoice: Button

    private var spinnerProgLang: Spinner? = null
    private var spinnerAppLang: Spinner? = null

    private val chatList = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter

    private var isVoiceEnabled = true

    // NOTA DE SEGURIDAD: Cuando vayas a compilar para la Play Store o subir a GitHub,
    // recuerda cambiar este string por BuildConfig.GEMINI_API_KEY como configuramos antes.
    private val geminiApiKey = "TU_API_KEY_DE_GOOGLE_AQUI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        textToSpeech = TextToSpeech(this, this)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        editMessage = findViewById(R.id.editMessage)
        btnSend = findViewById(R.id.btnSend)
        editCode = findViewById(R.id.editCode)
        btnRunCode = findViewById(R.id.btnRunCode)
        btnCheckCode = findViewById(R.id.btnCheckCode)
        btnChallenge = findViewById(R.id.btnChallenge)
        btnDetail = findViewById(R.id.btnDetail)
        btnToggleVoice = findViewById(R.id.btnToggleVoice)

        spinnerProgLang = findViewById(R.id.spinnerProgLang)
        spinnerAppLang = findViewById(R.id.spinnerAppLang)

        setupSpinners()

        chatAdapter = ChatAdapter(chatList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        val welcomeText = "¡Hola! Soy Mora. Selecciona un lenguaje y programemos juntos."
        chatList.add(Message(welcomeText, false))
        chatAdapter.notifyItemInserted(chatList.size - 1)

        btnToggleVoice.setOnClickListener {
            isVoiceEnabled = !isVoiceEnabled
            if (isVoiceEnabled) {
                btnToggleVoice.text = "🔊"
                if (::textToSpeech.isInitialized) {
                    textToSpeech.speak("Voz activada", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            } else {
                btnToggleVoice.text = "🔇"
                if (::textToSpeech.isInitialized) {
                    textToSpeech.stop()
                }
            }
        }

        btnSend.setOnClickListener {
            val userText = editMessage.text.toString().trim()
            if (userText.isNotEmpty()) {
                appendUserMessage(userText)
                editMessage.text.clear()
                sendToGemini(userText)
            }
        }

        btnRunCode.setOnClickListener {
            val codeText = editCode.text.toString().trim()
            if (codeText.isNotEmpty()) {
                appendUserMessage("Probar código del editor")
                sendToGemini("Por favor revisa y analiza este código que escribí:\n$codeText")
            }
        }

        btnCheckCode.setOnClickListener {
            appendUserMessage("🔍 Revisar Código")
            sendToGemini("Por favor revisa el código que tengo en el editor y dime si hay errores.")
        }
        btnChallenge.setOnClickListener {
            appendUserMessage("⭐ Nuevo Reto")
            sendToGemini("Por favor dame un nuevo reto de programación interactivo para practicar.")
        }
        btnDetail.setOnClickListener {
            appendUserMessage("💡 Explicación detallada")
            sendToGemini("Explícame en detalle paso a paso el último concepto que vimos.")
        }
    }

    private fun setupSpinners() {
        spinnerProgLang?.let { spinner ->
            val progLangs = arrayOf("Python", "JavaScript", "HTML", "CSS", "SQL")
            // Ahora utiliza el layout personalizado que creaste (spinner_item_blanco.xml)
            val adapter = ArrayAdapter(this, R.layout.spinner_item_blanco, progLangs)
            adapter.setDropDownViewResource(R.layout.spinner_item_blanco)
            spinner.adapter = adapter
        }

        spinnerAppLang?.let { spinner ->
            val appLangs = arrayOf("Español", "Inglés", "Alemán", "Japonés", "Francés")
            // Ahora utiliza el layout personalizado que creaste (spinner_item_blanco.xml)
            val adapter = ArrayAdapter(this, R.layout.spinner_item_blanco, appLangs)
            adapter.setDropDownViewResource(R.layout.spinner_item_blanco)
            spinner.adapter = adapter
        }
    }

    private fun appendUserMessage(text: String) {
        chatList.add(Message(text, true))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        chatRecyclerView.scrollToPosition(chatList.size - 1)
    }

    private fun sendToGemini(message: String) {
        val codeContent = editCode.text.toString()

        val programmingLanguage = spinnerProgLang?.selectedItem?.toString() ?: "Python"
        val appLanguage = spinnerAppLang?.selectedItem?.toString() ?: "Español"

        val typingIndicator = Message("Mora está pensando... 🐾💭", false)
        chatList.add(typingIndicator)
        val typingIndex = chatList.size - 1
        chatAdapter.notifyItemInserted(typingIndex)
        chatRecyclerView.scrollToPosition(typingIndex)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // MODELO INYECTADO: 3.5 Flash-Lite para máxima velocidad y estabilidad
                val generativeModel = GenerativeModel(
                    modelName = "gemini-3.5-flash-lite",
                    apiKey = geminiApiKey,
                    generationConfig = generationConfig {
                        maxOutputTokens = 400
                        temperature = 0.7f
                    }
                )

                val prompt = """
                    Eres Mora, una tutora de programación amigable, moderna y motivadora.
                    Estás enseñando y ayudando al estudiante con el lenguaje de programación: $programmingLanguage.
                    
                    Código actual del estudiante: $codeContent
                    Mensaje del estudiante: $message
                    
                    REGLAS CRÍTICAS: 
                    1. Responde 100% en el idioma: $appLanguage.
                    2. Tus explicaciones y correcciones deben estar basadas estrictamente en $programmingLanguage.
                    3. NO te presentes. Ve directo a la respuesta.
                    4. SÉ MUY CONCISA Y RÁPIDA. Da explicaciones cortas y directas.
                    5. Guía al estudiante paso a paso, usa emojis.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val reply = response.text ?: "¡Ups! Ocurrió un error al generar la respuesta."

                withContext(Dispatchers.Main) {
                    chatList[typingIndex] = Message(reply, false)
                    chatAdapter.notifyItemChanged(typingIndex)
                    chatRecyclerView.scrollToPosition(typingIndex)

                    speakCleanText(reply, appLanguage)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = ex.message ?: ""
                    val friendlyError = when {
                        errorMessage.contains("503") || errorMessage.contains("demand") ->
                            "Mora está recibiendo muchas consultas en este momento. 🐾 ¡Reintenta en unos segundos!"
                        errorMessage.contains("Quota") || errorMessage.contains("429") ->
                            "Mora necesita un respiro. 🐾 ¡Has hecho muchas consultas seguidas! Espera un minuto."
                        errorMessage.contains("403") ->
                            "Error de autenticación con la API Key. Verifica tu clave. 🐾"
                        else ->
                            "ERROR TÉCNICO: $errorMessage"
                    }

                    chatList[typingIndex] = Message(friendlyError, false)
                    chatAdapter.notifyItemChanged(typingIndex)
                    chatRecyclerView.scrollToPosition(typingIndex)
                }
            }
        }
    }

    private fun speakCleanText(text: String, language: String) {
        if (!isVoiceEnabled) return

        val cleaned = text
            .replace(Regex("[\\p{So}]"), "")
            .replace(Regex("[*#_`~()\\[\\]]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (::textToSpeech.isInitialized) {
            val locale = when (language.lowercase()) {
                "inglés" -> Locale.ENGLISH
                "alemán" -> Locale.GERMAN
                "japonés" -> Locale.JAPANESE
                "francés" -> Locale.FRENCH
                else -> Locale("es", "ES")
            }
            textToSpeech.language = locale
            textToSpeech.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale("es", "ES")
            textToSpeech.setPitch(1.12f)
            textToSpeech.setSpeechRate(0.92f)
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}