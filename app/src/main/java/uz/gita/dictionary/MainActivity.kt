package uz.gita.dictionary

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Trace.isEnabled
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import uz.gita.dictionary.adapter.DictionaryAdapter

import uz.gita.dictionary.repository.AppRepository
import java.util.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val repository = AppRepository.getRepository()
    private lateinit var adapter : DictionaryAdapter
    private var querySt =""
    private lateinit var handler : Handler
    private lateinit var voice : TextToSpeech

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        adapter = DictionaryAdapter(repository.getDictionaryCursor(querySt), querySt)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        adapter.setClickFavouriteItemListener {
            repository.updateFavourite(it)
            adapter.cursor = repository.getDictionaryCursor(querySt)
            adapter.notifyDataSetChanged()
        }
        voice = TextToSpeech(this){

        }
        adapter.setClickVoiceItemListener {data, image->
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

        more.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

        adapter.setClickItemListener { data, pos ->
            val intent = Intent(this, DefinitionActivity::class.java)
            intent.putExtra("id", data.id)
            intent.putExtra("word", data.word)
            intent.putExtra("type", data.wordtype)
            intent.putExtra("definition", data.definition)
            intent.putExtra("isRemember",data.isRemember)
            startActivity(intent)
        }

        handler = Handler(Looper.getMainLooper())
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
               handler.removeCallbacksAndMessages(null)
                query?.let {
                    querySt = it.trim()
                    adapter.cursor = repository.getDictionaryCursor(querySt)
                    adapter.query = querySt
                    adapter.notifyDataSetChanged()
                    searchView.setQuery(querySt, false)
                }
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    newText?.let {
                        querySt = it.trim()
                        adapter.cursor = repository.getDictionaryCursor(querySt)
                        if (adapter.cursor.count == 0){
                            searchView.clearFocus()
                            no_result.visibility = View.VISIBLE
                        }else {
                            searchView.isFocused
                            no_result.visibility = View.INVISIBLE
                        }
                        adapter.query = querySt
                        adapter.notifyDataSetChanged()
                        searchView.setQuery(querySt, false)
                    }
                }, 300)
                return true
            }

        })

        val closeBtn = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeBtn.setOnClickListener {
            searchView.setQuery(null, false)
            searchView.clearFocus()
            this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
    }


    override fun onPause() {
        super.onPause()
        voice.stop()
        voice.shutdown()
        handler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStop() {
        super.onStop()
        adapter.cursor = repository.getDictionaryCursor(querySt)
        adapter.notifyDataSetChanged()
    }

}