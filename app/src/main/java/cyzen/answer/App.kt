package cyzen.answer

import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context

        fun getString(resId: Int): String = context.getString(resId)
        fun getText(resId: Int): CharSequence = context.getText(resId)
    }

}