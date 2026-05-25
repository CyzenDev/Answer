package cyzen.utils

import android.os.Build
import java.io.File
import java.util.Locale

/**
 * ## sys分区是否可读
 */
val isSysReadable: Boolean get() = File("/sys").canRead()

/**
 * ## 是否为工程模式
 */
val IsEngBuild = "eng" == Build.TYPE


/**
 * ## 系统语言是否为中文
 */
val isZH: Boolean
    get() = Locale.getDefault().language.contains("zh")


/**
 * # Sdk stuff
 */
val SDK_INT = Build.VERSION.SDK_INT
val isMarshmallowOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
val isNougatOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
val isOreoOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
val isPieOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
val isQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
val isROrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
val isSOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
val isSv2OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
val isTiramisuOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
val isUpsideOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
val isVanillaOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
val isPreviewSdk = "REL" != Build.VERSION.CODENAME
