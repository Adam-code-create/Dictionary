package uz.gita.dictionary.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import uz.gita.dictionary.app.App


fun color (@ColorRes colorRes: Int) : Int = ContextCompat.getColor(App.instance, colorRes)

//fun color(@ColorRes colorResID : Int) : Int =
//    ContextCompat.getColor(App.instance, colorResID)