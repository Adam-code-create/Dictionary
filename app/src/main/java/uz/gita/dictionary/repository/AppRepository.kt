package uz.gita.dictionary.repository

import android.database.Cursor
import uz.gita.dictionary.data.database.MyDatabase
import uz.gita.dictionary.data.model.DataDictionary
import uz.gita.dictionary.data.sharedPref.SharedPref

class AppRepository  private constructor() {
    private val pref = SharedPref.getSharedPref()
    private val database =  MyDatabase.getDatabase()

    companion object {
        private lateinit var instance : AppRepository

        fun getRepository() : AppRepository {
            if (!::instance.isInitialized){
                instance= AppRepository()
            }
            return instance
        }
    }

    fun getDictionaryCursor ( query : String) : Cursor = database.getDictionaryCursor(query)
    fun getFavouriteCursor () : Cursor = database.getFavouriteCursor()
    fun updateFavourite (data: DataDictionary) {
        database.updateFavourite(data)
    }



}