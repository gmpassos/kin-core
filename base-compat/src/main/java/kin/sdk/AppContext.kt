package kin.sdk

import java.io.File;

/**
 * Substitute of `android.content.Context`:
 * https://developer.android.com/reference/android/content/Context
 */
abstract class AppContext {

    companion object {
        const val MODE_PRIVATE = 0 ;
    }

    abstract val applicationContext: AppContext ;

    abstract val filesDir: File;

    abstract fun getSharedPreferences(name: String, mode: Int): SharedPreferences;

}

interface SharedPreferences {

    fun getString(key: String, defValue: String?): String?

    fun edit(): Editor;

    interface Editor {
        fun putString(key: String, value: String): Editor

        fun remove(key: String): Editor

        fun apply()
    }

}