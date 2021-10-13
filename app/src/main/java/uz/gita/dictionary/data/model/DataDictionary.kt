package uz.gita.dictionary.data.model


data class DataDictionary(
    val id : Int,
    val word: String,
    val wordtype : String,
    val definition: String,
    val isRemember :Int
)
