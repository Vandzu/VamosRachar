package com.example.vamosrachar

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var peopleInput: TextInputEditText
    private lateinit var valueInput: TextInputEditText
    private lateinit var resultText: TextView
    private lateinit var tts : TextToSpeech
    private var btnSpeak: Button? = null
    private lateinit var btnShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        peopleInput = findViewById<TextInputEditText>(R.id.people)
        valueInput = findViewById<TextInputEditText>(R.id.value)
        resultText = findViewById<TextView>(R.id.result)
        verifyChange(peopleInput)
        verifyChange(valueInput)

        btnSpeak = findViewById(R.id.TTS)
        btnSpeak!!.isEnabled = false
        tts = TextToSpeech(this, this)
        btnSpeak!!.setOnClickListener {speakOut()}

        btnShare = findViewById<Button>(R.id.share)
        btnShare.setOnClickListener {
            if (resultText.text != "R$ 0,00") {
                val intent = Intent(Intent.ACTION_SEND)
                val qttPeople = peopleInput.text
                val qttValue = valueInput.text
                val qttResult = resultText.text
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "O valor total de $qttValue para $qttPeople pessoas: $qttResult para cada")
                startActivity(Intent.createChooser(intent, "Compartilhar texto"))
            }
        }
    }

    fun verifyChange(text: TextInputEditText) {
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //ok
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                if (!valueInput.text.toString().isNullOrBlank() && !peopleInput.text.toString().isNullOrBlank()) {
                    val result = valueInput.text.toString().toDouble() / peopleInput.text.toString().toDouble()
                    val currency = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                    val resultFormated = currency.format(result)
                    resultText.text = resultFormated
                } else if (!resultText.text.isNullOrBlank()) {
                    resultText.text = "R$ 0,00"
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Chamado depois que o texto é alterado
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                btnSpeak!!.isEnabled = true
            }
        }
    }

    private fun speakOut() {
        val text = resultText.text.toString()
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,null)
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}