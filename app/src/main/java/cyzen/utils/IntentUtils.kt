package cyzen.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable


/**
 * Safely and simply startActivity
 */
fun Context?.startAct(intent: Intent) =
    tryCatch({ this?.startActivity(intent) }, { printTrace() })

inline fun <reified T> Context?.startAct() =
    tryCatch({ this?.startActivity(Intent(this, T::class.java)) }, { printTrace() })

inline fun <reified T> Context?.startAct(name: String, value: Int) =
    tryCatch({
        this?.startActivity(Intent(this, T::class.java).putExtra(name, value))
    }, { printTrace() })

inline fun <reified T> Context?.startAct(name: String, value: String) =
    tryCatch({
        this?.startActivity(Intent(this, T::class.java).putExtra(name, value))
    }, { printTrace() })

inline fun <reified T> Context?.startAct(name: String, value: Boolean) =
    tryCatch({
        this?.startActivity(Intent(this, T::class.java).putExtra(name, value))
    }, { printTrace() })

inline fun <reified T> Context?.startAct(name: String, value: Serializable) =
    tryCatch({
        this?.startActivity(Intent(this, T::class.java).putExtra(name, value))
    }, { printTrace() })

inline fun <reified T> Context?.startAct(name: String, value: Parcelable) =
    tryCatch({
        this?.startActivity(Intent(this, T::class.java).putExtra(name, value))
    }, { printTrace() })


/**
 *  [Intent.getIntExtra] with default value 0
 */
fun Activity.getIntExtra(name: String, defValue: Int = 0): Int =
    intent.getIntExtra(name, defValue)

/**
 * Safely **getStringExtra**
 */
fun Activity.getStringExtra(name: String): String? =
    tryCatchRun({
        intent.getStringExtra(name)
    }, {
        printStackTrace()
        null
    })

/**
 * Safely **getSerializableExtra**
 */
inline fun <reified T : Serializable> Activity.getSerializableExtra(name: String): T? =
    tryCatchRun({
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, T::class.java)
        else intent.getSerializableExtra(name) as T?
    }, {
        printStackTrace()
        null
    })

/**
 * Safely **getParcelableExtra**
 */
inline fun <reified T : Parcelable> Activity.getParcelableExtra(name: String): T? =
    tryCatchRun({
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(name, T::class.java)
        else intent.getParcelableExtra(name) as T?
    }, {
        printStackTrace()
        null
    })