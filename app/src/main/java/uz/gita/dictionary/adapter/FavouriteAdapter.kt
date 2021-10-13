package uz.gita.dictionary.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_favourite.view.*
import uz.gita.dictionary.R
import uz.gita.dictionary.data.model.DataDictionary
import uz.gita.dictionary.utils.color

class FavouriteAdapter  (
    var cursor :Cursor,
    var query  : String
        ) : RecyclerView.Adapter<FavouriteAdapter.DictionaryViewHolder>(){

    private var clickItemListener : ((DataDictionary, Int) -> Unit)?= null
    private var deleteItemListener : ((DataDictionary) -> Unit)?= null
    private var clickVoiceItemListener : ((DataDictionary, ImageView) -> Unit)?= null


            inner class DictionaryViewHolder (view:View):RecyclerView.ViewHolder(view){
                init {
                    itemView.setOnClickListener {
                       if (adapterPosition > -1) clickItemListener?.invoke(getDataByPosition(adapterPosition), adapterPosition)
                    }
                    itemView.delete.setOnClickListener {
                        if (adapterPosition > -1) deleteItemListener?.invoke(getDataByPosition(adapterPosition))
                    }
                    itemView.voiceTrash.setOnClickListener {
                        if (adapterPosition > -1) clickVoiceItemListener?.invoke(getDataByPosition(adapterPosition),itemView.voiceTrash)

                    }

                }

                fun bind(data: DataDictionary){
                    val spanSt = SpannableString(data.word)
                    val spanColor = ForegroundColorSpan(color(R.color.purple_700))
                    val startIndex = data.word.indexOf(query, 0, true)
                    val lastIndex = startIndex + query.length
                    spanSt.setSpan(spanColor,startIndex, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    itemView.wordFavourite.text = spanSt


                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
        return DictionaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        holder.bind(getDataByPosition(position))
    }

    override fun getItemCount(): Int = cursor.count

    @SuppressLint("Range")
    private fun getDataByPosition (pos :Int) : DataDictionary{
        cursor.moveToPosition(pos)
        return DataDictionary(
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getString(cursor.getColumnIndex("word")),
            cursor.getString(cursor.getColumnIndex("wordtype")),
            cursor.getString(cursor.getColumnIndex("definition")),
            cursor.getInt(cursor.getColumnIndex("isRemember"))
        )
    }

    fun setClickItemListener (f: (DataDictionary, Int) -> Unit ){
        clickItemListener = f
    }
    fun setDeleteItemListener (f: (DataDictionary) -> Unit ){
        deleteItemListener = f
    }
    fun setClickVoiceItemListener (f :(DataDictionary, ImageView)-> Unit){
        clickVoiceItemListener = f
    }




}