package uz.gita.dictionary

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_favourite.*
import uz.gita.dictionary.adapter.DictionaryAdapter
import uz.gita.dictionary.adapter.FavouriteAdapter
import uz.gita.dictionary.repository.AppRepository
import java.util.*

class FavouriteActivity : AppCompatActivity() {
    private val repository = AppRepository.getRepository()
    private lateinit var adapter : FavouriteAdapter
    private var querySt =""
    private lateinit var voice : TextToSpeech
    private lateinit var handler : Handler
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        adapter = FavouriteAdapter(repository.getFavouriteCursor(), querySt)
        listFavourite.adapter = adapter
        listFavourite.layoutManager = LinearLayoutManager(this)


        adapter.setClickItemListener { data, pos ->
            favourite.text = data.word
            favouriteDefinition.text = data.definition
            favouriteDefinition.visibility = View.VISIBLE
        }
        voice = TextToSpeech(this){

        }
        adapter.setClickVoiceItemListener { data, image ->
            voice = TextToSpeech(this){
                if (it == TextToSpeech.SUCCESS){
                    val result = voice.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(this, "Language not Supported", Toast.LENGTH_SHORT).show()
                    }else{
                        image.isEnabled = true
                    }
                }
                voice.speak(data.word,TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        adapter.setDeleteItemListener {data->
            repository.updateFavourite(data)
            adapter.cursor = repository.getFavouriteCursor()
            adapter.notifyDataSetChanged()
            if (repository.getFavouriteCursor().count == 0){
                favourite.text = "Favourite"
                favouriteDefinition.visibility = View.GONE
            }
        }




        backFavourite.setOnClickListener {
            finish()
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        if (voice!= null){
            voice.stop()
            voice.shutdown()
        }
    }
}