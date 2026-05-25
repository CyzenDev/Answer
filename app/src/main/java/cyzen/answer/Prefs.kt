package cyzen.answer

import android.content.Context
import android.content.SharedPreferences


object Prefs {
    private const val PREFS_NAME = "prefs"


    /**
     * 答案显示模式
     */
    var answerDisplayType: Int
        get() = preferences.getInt(ANSWER_DISPLAY_TYPE, 4)
        set(value) = edit.putInt(ANSWER_DISPLAY_TYPE, value).apply()
    private const val ANSWER_DISPLAY_TYPE = "answer_display_type"

    /**
     * 自动保存题目
     */
    var saveQuiz: Boolean
        get() = preferences.getBoolean(SAVE_QUIZ, false)
        set(value) = edit.putBoolean(SAVE_QUIZ, value).apply()
    private const val SAVE_QUIZ = "save_quiz"


    /**
     * Main SharedPreferences, set open
     */
    val preferences: SharedPreferences by lazy {
        App.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Main SharedPreferences.Editor, set open
     */
    val edit: SharedPreferences.Editor by lazy { preferences.edit() }
}