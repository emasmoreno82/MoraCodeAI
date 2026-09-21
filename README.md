# Mora Code AI 🐾 - Gemini API Developer Competition

Mora Code AI es una aplicación nativa de Android diseñada para ser una tutora de programación amigable, interactiva y de respuesta ultrarrápida. Creada por Geek Mode! Studios, Mora ayuda a los estudiantes a aprender a programar, depurar errores y superar retos directamente desde su celular.

## 🚀 Características Principales
* **Tutoría Multilenguaje:** Soporte para Python, JavaScript, HTML, CSS y SQL.
* **Respuestas Ultrarrápidas:** Integración directa con **Gemini 3.5 Flash-Lite** para garantizar tiempos de respuesta menores a 3 segundos, ideal para el aprendizaje en tiempo real.
* **Voz Integrada (TTS):** Mora te habla y te explica los conceptos en voz alta (soporta Español, Inglés, Alemán, Japonés y Francés).
* **Análisis de Código:** Un editor de texto integrado donde el estudiante puede escribir código y pedirle a Mora que lo evalúe o busque errores.

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Kotlin
* **Plataforma:** Android Studio
* **Inteligencia Artificial:** SDK oficial de Google Generative AI (Modelo: `gemini-3.5-flash-lite`).
* **Accesibilidad:** Text-to-Speech (TTS) nativo de Android.

## ⚙️ Cómo ejecutar este proyecto (Instrucciones para Jueces)

Para probar Mora Code AI en tu propio dispositivo o emulador, sigue estos pasos:

1. Clona este repositorio en Android Studio.
2. Genera una API Key gratuita en [Google AI Studio](https://aistudio.google.com/).
3. Abre el archivo `MainActivity.kt` ubicado en `app/src/main/java/com/example/moracodeai/`.
4. Busca la variable `geminiApiKey` (cerca de la línea 40) y reemplaza el texto con tu clave generada:
   ```kotlin
   private val geminiApiKey = "TU_API_KEY_DE_GOOGLE_AQUI"
