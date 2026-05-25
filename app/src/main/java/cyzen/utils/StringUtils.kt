package cyzen.utils

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan


/**
 * 设置字符串特定位置的字体为[android.graphics.Typeface.SERIF]
 */
fun SpannableString.setSerifSpan(start: Int, end: Int = length) = this.apply {
    setSpan(TypefaceSpan("serif"), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/**
 * 设置字符串特定位置的字体为[android.graphics.Typeface.SANS_SERIF]
 */
fun SpannableString.setSansSerifSpan(start: Int, end: Int = length) = this.apply {
    setSpan(TypefaceSpan("sans-serif"), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/**
 * 设置字符串特定位置的相对字体大小
 *
 * @param proportion 相对比例
 */
fun SpannableString.setRelativeSizeSpan(proportion: Float, start: Int, end: Int = length) = this.apply {
    setSpan(RelativeSizeSpan(proportion), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
}


/**
 * 是否是两位小数
 */
fun String.isTwoDecimalPlaces(): Boolean =
    tryCatchRun({
        split("\\.".toRegex())[1].length <= 2
    }, {
        false
    })


/**
 * 一些Strings工具类方法的增强版
 */


/**
 * Returns `true` if this nullable char sequence is neither `null` nor empty.
 */
fun CharSequence?.isNotNullOrEmpty(): Boolean = this != null && this.isNotEmpty()

/**
 * Returns a new string with the last occurrence of [oldChar] replaced with [newChar].
 *
 * @see [replaceFirst]
 */
fun String.replaceLast(oldChar: Char, newChar: Char, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldChar, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + 1, newChar.toString())
}

/**
 * Returns a new string obtained by replacing the last occurrence of the [oldValue] substring in this string
 * with the specified [newValue] string.
 *
 * @see [replaceFirst]
 */
fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}

/**
 * Returns a new string with the last occurrence of [oldChar] replaced with [newChar].
 *
 * @see [replaceFirst]
 */
fun CharSequence.replaceLast(oldChar: Char, newChar: Char, ignoreCase: Boolean = false): CharSequence {
    val index = lastIndexOf(oldChar, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + 1, newChar.toString())
}

/**
 * Returns a new string obtained by replacing the last occurrence of the [oldValue] substring in this string
 * with the specified [newValue] string.
 *
 * @see [replaceFirst]
 */
fun CharSequence.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): CharSequence {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}
