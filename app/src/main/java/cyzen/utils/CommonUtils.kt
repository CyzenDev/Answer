package cyzen.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cyzen.answer.App
import cyzen.answer.BuildConfig

/**
 * 让[SharedPreferences.getString]为空时返回默认值
 */
fun SharedPreferences.getMyString(key: String, defValue: String): String =
    getString(key, defValue) ?: defValue

/**
 * 适配新老版本的[PackageManager.getPackageInfo]
 */
fun PackageManager.getPackageInfo(packageName: String): PackageInfo =
    if (isTiramisuOrLater)
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    else getPackageInfo(packageName, 0)

/**
 * 适配新老版本的[PackageInfo.versionCode]
 */
fun PackageInfo.getVersionCode(): Long =
    if (isPieOrLater) longVersionCode
    else versionCode.toLong()

/**
 * 是否安装了应用
 *
 * @param packageName 包名
 */
fun Context?.hasInstalledApp(packageName: String?): Boolean = tryCatchRun({
    if (this == null || packageName.isNullOrEmpty()) false
    else packageManager.getPackageInfo(packageName, 0) != null
}, { false })


/**
 * 是否是debug版本
 *
 * 需要在buildFeatures中添加`buildConfig = true`
 */
const val isDebugBuild = "debug" == BuildConfig.BUILD_TYPE

/**
 * 打印异常堆栈跟踪信息，只在debug版本中打印
 */
fun Throwable.printTrace(showToast: Boolean = false) {
    if (isDebugBuild) {
        printStackTrace()
        if (showToast) App.context.toast(message)
    }
}


val mainHandler by lazy { Handler(Looper.getMainLooper()) }


private var mToast: Toast? = null

fun Context?.toast(textId: Int, longDuration: Boolean = false) =
    this?.apply { toast(getString(textId), longDuration) }

fun Context?.toast(text: CharSequence?, longDuration: Boolean = false) {
    mainHandler.post {
        try {
            if (this == null || text.isNullOrEmpty()) return@post
            mToast?.cancel()
            mToast = Toast.makeText(this, text, if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).apply { show() }
        } catch (_: Exception) {
        }
    }
}
