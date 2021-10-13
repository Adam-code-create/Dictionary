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
import kotlinx.android.synthetic.main.word_space_item.view.*
import uz.gita.dictionary.R
import uz.gita.dictionary.data.model.DataDictionary
import uz.gita.dictionary.utils.color

class DictionaryAdapter  (
    var cursor :Cursor,
    var query  : String
        ) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>(){

    private var clickItemListener : ((DataDictionary, Int) -> Unit)?= null
    private var clickFavouriteItemListener : ((DataDictionary) -> Unit)?= null
    private var clickVoiceItemListener : ((DataDictionary, ImageView) -> Unit)?= null

            inner class DictionaryViewHolder (view:View):RecyclerView.ViewHolder(view){
                init {
                    itemView.setOnClickListener {
                        if (adapterPosition > -1)clickItemListener?.invoke(getDataByPosition(adapterPosition), adapterPosition)
                    }
                    itemView.bookmark.setOnClickListener {
                        if (adapterPosition > -1) clickFavouriteItemListener?.invoke(getDataByPosition(adapterPosition))

                    }
                    itemView.voice.setOnClickListener {
                        if (adapterPosition > -1)clickVoiceItemListener?.invoke(getDataByPosition(adapterPosition),itemView.voice)

                    }
                }

                fun bind(data: DataDictionary){
                    val spanSt = SpannableString(data.word)
                    val spanColor = ForegroundColorSpan(color(R.color.span))
                    val startIndex = data.word.indexOf(query, 0, true)
                    val lastIndex = startIndex + query.length
                    spanSt.setSpan(spanColor,startIndex, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    itemView.word.text = spanSt
                    if (data.isRemember == 1) {
                        //  data.isFavourite = 1
                        itemView.bookmark.setImageResource(R.drawable.ic_bookmark_selected)
                    } else {
                        //   data.isFavourite = 0
                        itemView.bookmark.setImageResource(R.drawable.ic_bookmark)
                    }

                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_space_item, parent, false)
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
    fun setClickFavouriteItemListener (f :(DataDictionary)-> Unit){
        clickFavouriteItemListener = f
    }

    fun setClickVoiceItemListener (f :(DataDictionary, ImageView)-> Unit){
        clickVoiceItemListener = f
    }



}