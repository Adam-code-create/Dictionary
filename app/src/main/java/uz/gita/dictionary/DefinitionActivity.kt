package uz.gita.dictionary

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_definition.*
import kotlinx.android.synthetic.main.word_space_item.view.*
import uz.gita.dictionary.data.model.DataDictionary
import uz.gita.dictionary.repository.AppRepository
import java.util.*

class DefinitionActivity : AppCompatActivity() {
    private val repository = AppRepository.getRepository()
    private lateinit var voice : TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definition)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val bundle = intent.extras!!
        val id = bundle.getInt("id")
        val word = bundle.getString("word")
        val typeWord = bundle.getString("type")
        val definition = bundle.getString("definition")
        var isRemember = bundle.getInt("isRemember")

        val copytext = "$word  :  $definition"
        titleDefinition.text = word
        dicWord.text = word
        wordType.text = typeWord
        definitionWord.text = definition
         voice = TextToSpeech(this){

         }
        voiceDefinition.setOnClickListener {
            voice = TextToSpeech(this){
                if (it == TextToSpeech.SUCCESS){
                    val result = voice.setLanguage(Locale.ENGLISH)

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(this, "Language not Supported", Toast.LENGTH_SHORT).show()
                    }else{
                        voiceDefinition.isEnabled = true
                    }
                }
                voice.speak(word,TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        backMain.setOnClickListener {
            finish()
        }

        if (isRemember == 1){
            isfavourite.setImageResource(R.drawable.ic_bookmark_selected)
        } else isfavourite.setImageResource(R.drawable.ic_bookmark2)

        copy.setOnClickListener {
            val clipText = ClipData.newPlainText("text",copytext)
            val myClip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            myClip.setPrimaryClip(clipText)
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, copytext)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        isfavourite.setOnClickListener {
            if (isRemember == 1) {
                repository.updateFavourite(DataDictionary(id,word!!, typeWord!!,definition!!, 1))
                isfavourite.setImageResource(R.drawable.ic_bookmark2)
                isRemember = 0
            }
             else {
                repository.updateFavourite(DataDictionary(id,word!!, typeWord!!,definition!!, 0))
                 isfavourite.setImageResource(R.drawable.ic_bookmark_selected)
                isRemember = 1
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        voice.stop()
        voice.shutdown()
    }
}