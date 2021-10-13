package uz.gita.dictionary.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import uz.gita.dictionary.app.App
import uz.gita.dictionary.data.model.DataDictionary

class MyDatabase private constructor(context: Context, listener: EventListener): DBHelper(context, "Lugat.db", 1, listener) {

    @SuppressLint("StaticFieldLeak")
    companion object{
        private var finishListener : (() -> Unit)? =null
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance : MyDatabase
        fun getDatabase () : MyDatabase{
            if (!::instance.isInitialized){
                instance = MyDatabase(App.instance){
                    Log.d("TTT","lambda")
                    finishListener?.invoke()
                }
            }
            return instance
        }

        fun setFinishListener(f : () -> Unit) {
            finishListener = f
        }
    }

    fun updateFavourite (data :DataDictionary){
        val cv = ContentValues()
        if (data.isRemember==0) cv.put("isRemember", 1)
        else cv.put("isRemember", 0)
        instance.database().update("entries", cv, "entries.id = ${data.id}", null)
    }

    fun getDictionaryCursor (query :String) : Cursor{
        val querySt = "SELECT * FROM entries WHERE entries.word LIKE '%$query%'"
        return instance.database().rawQuery(querySt, null)
    }

    fun getFavouriteCursor() : Cursor {
        val querySt = "SELECT * FROM entries WHERE entries.isRemember = 1"
        return instance.database().rawQuery(querySt, null)
    }
}