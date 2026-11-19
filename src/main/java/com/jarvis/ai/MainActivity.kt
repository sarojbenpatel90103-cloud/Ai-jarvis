
package com.jarvis.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private lateinit var answer: TextView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(28, 35, 28, 28)
        layout.setBackgroundColor(Color.rgb(5, 13, 30))

        val title = TextView(this)
        title.text = "J.A.R.V.I.S."
        title.textSize = 32f
        title.setTextColor(Color.CYAN)
        title.gravity = Gravity.CENTER

        answer = TextView(this)
        answer.text = "Hello, Sir! How can I help you?"
        answer.textSize = 20f
        answer.setTextColor(Color.WHITE)
        answer.gravity = Gravity.CENTER
        answer.setPadding(10, 35, 10, 35)

        input = EditText(this)
        input.hint = "Type your command, Sir..."
        input.setHintTextColor(Color.LTGRAY)
        input.setTextColor(Color.WHITE)
        input.setSingleLine(true)

        val send = Button(this)
        send.text = "SEND COMMAND"

        val speak = Button(this)
        speak.text = "SPEAK TO JARVIS"

        layout.addView(title)
        layout.addView(answer)
        layout.addView(input)
        layout.addView(send)
        layout.addView(speak)

        setContentView(layout)

        send.setOnClickListener {
            respond(input.text.toString())
        }

        speak.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO), 10
                )
            } else {
                listen()
            }
        }

        say("Hello, Sir. JARVIS is ready.")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("en", "IN")
            tts?.setSpeechRate(0.9f)
        }
    }

    private fun listen() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "en-IN"
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Hello, Sir. I am listening."
            )
            startActivityForResult(intent, 20)
        } catch (e: Exception) {
            answer.text = "Speech recognition is unavailable."
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 20 && resultCode == RESULT_OK) {
            val words = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            if (!words.isNullOrEmpty()) {
                respond(words[0])
            }
        }
    }

    private fun respond(command: String) {
        val text = command.lowercase(Locale.ROOT)

        val reply = when {
            text.contains("hello") || text.contains("hi") ->
                "Hello, Sir. How can I help you?"

            text.contains("how are you") ->
                "I am functioning perfectly, Sir."

            text.contains("your name") ->
                "I am JARVIS, your personal assistant."

            text.contains("time") ->
                "The time is " +
                java.text.SimpleDateFormat(
                    "hh:mm a", Locale.getDefault()
                ).format(java.util.Date())

            else ->
                "I heard you say: $command. My AI brain will be connected in the next stage, Sir."
        }

        answer.text = reply
        say(reply)
    }

    private fun say(message: String) {
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
