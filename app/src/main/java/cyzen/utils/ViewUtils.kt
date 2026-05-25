package cyzen.utils

import android.app.Activity
import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView

/**
 * 反射开启Menu的图标显示
 */
fun Menu.setOptionalIconsVisible(enable: Boolean) = tryCatch {
    Class.forName("com.android.internal.view.menu.MenuBuilder").getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java).apply {
        isAccessible = true
        invoke(this@setOptionalIconsVisible, enable)
    }
}

/**
 * 获取[ListView]的scrollY
 */
fun ListView?.getScrollY(): Int =
    this?.getChildAt(0)?.let { view ->
        -view.top + firstVisiblePosition * view.height
    } ?: 0

/**
 * 获取状态栏高度
 */
fun Context?.getStatusBarHeight(): Int =
    this?.resources?.getIdentifier("status_bar_height", "dimen", "android")?.run {
        if (this > 0) resources.getDimensionPixelSize(this) else 30
    } ?: 30


/**
 * Hide input method for View, e.g. [android.widget.EditText]
 */
fun View?.hideIme() = tryCatch {
    this?.apply {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
    }
}

/**
 * Perform a haptic feedback
 */
fun View?.performHapticFeedback() = this?.apply {
    isHapticFeedbackEnabled = true
    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
}

/**
 * For debug, print pos
 */
fun View.printPos(name: String = "View") = post {
    if (this is TextView) println("$name.textSize=$textSize")
    val arr1 = IntArray(2).apply { getLocationInWindow(this) }
    val arr2 = IntArray(2).apply { getLocationOnScreen(this) }
    println("$name-> x=$x  y=$y  top=$top  bottom=$bottom  left=$left  right=$right  locationInWindow=${arr1[0]},${arr1[1]}  locationOnScreen=${arr2[0]},${arr2[1]}")
}

/**
 * 设置[TextView]的字体样式
 */
fun TextView.setAppearance(resId: Int) =
    if (isMarshmallowOrLater) setTextAppearance(resId)
    else setTextAppearance(context, resId)


/**
 * 获取颜色[Context.getColor]
 */
fun Context.getMyColor(resId: Int): Int = resources.run {
    if (isMarshmallowOrLater) getColor(resId, theme)
    else getColor(resId)
}


/**
 * Override screen brightness for this activity.
 *
 * The brightness value should between **1** and **100**, **-1** means disable override.
 *
 * Once the value set, it will **not** change with the system.
 *
 * @param brightnessValue the brightness value in percent
 */
fun Activity.setScreenBrightness(brightnessValue: Int) = window?.apply {
    attributes = attributes.apply { screenBrightness = brightnessValue / 100f }
}

fun Context.dp2px(dp: Float): Int = (dp * resources.displayMetrics.density + 0.5f).toInt() - 1

fun Context.px2dp(px: Float): Int = (px / resources.displayMetrics.density + 0.5f).toInt()

fun Context.px2sp(px: Float): Int = (px / resources.displayMetrics.scaledDensity + 0.5f).toInt()

fun Context.sp2px(sp: Float): Int = (sp * resources.displayMetrics.scaledDensity + 0.5f).toInt()


val View.isVisible
    get() = visibility == View.VISIBLE

fun View.show() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

val View.isInvisible
    get() = visibility == View.INVISIBLE

fun View.hide() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

val View.isGone
    get() = visibility == View.GONE

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}