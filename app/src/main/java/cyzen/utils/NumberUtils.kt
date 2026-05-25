package cyzen.utils

fun String?.toInt(def: Int = 0): Int =
    tryCatchRun({ if (this == null) def else Integer.parseInt(this) }) { def }

fun String?.toInt(radix: Int, def: Int = 0): Int =
    tryCatchRun({ if (this == null) def else Integer.parseInt(this, checkRadix(radix)) }) { def }

fun String?.toLong(def: Long = 0): Long =
    tryCatchRun({ if (this == null) def else java.lang.Long.parseLong(this) }) { def }

fun String?.toLong(radix: Int, def: Long = 0): Long =
    tryCatchRun({
        if (this == null) def else java.lang.Long.parseLong(this, checkRadix(radix))
    }) { def }

fun String?.toFloat(def: Float = 0f): Float =
    tryCatchRun({ if (this == null) def else java.lang.Float.parseFloat(this) }) { def }

fun String?.toDouble(def: Double = 0.0): Double =
    tryCatchRun({ if (this == null) def else java.lang.Double.parseDouble(this) }) { def }

internal fun checkRadix(radix: Int): Int =
    if (radix !in Character.MIN_RADIX..Character.MAX_RADIX)
        throw IllegalArgumentException("radix $radix was not in valid range ${Character.MIN_RADIX..Character.MAX_RADIX}")
    else radix