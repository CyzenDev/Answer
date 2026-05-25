package cyzen.utils


inline fun <R> tryCatchRun(block: () -> R, catchBlock: Exception.() -> R): R =
    try {
        block()
    } catch (e: Exception) {
        catchBlock(e)
    }

inline fun <T, R> T.tryCatchRun(block: T.() -> R, catchBlock: Exception.() -> R): R =
    try {
        block()
    } catch (e: Exception) {
        catchBlock(e)
    }

inline fun <T, R> tryCatchWith(receiver: T, block: T.() -> R, catchBlock: Exception.() -> R): R =
    try {
        receiver.block()
    } catch (e: Exception) {
        catchBlock(e)
    }

inline fun <T> T.tryCatchApply(block: T.() -> Unit, catchBlock: Exception.() -> Unit): T =
    try {
        block(this)
        this
    } catch (e: Exception) {
        catchBlock(e)
        this
    }

inline fun <T> T.tryCatchAlso(block: (T) -> Unit, catchBlock: Exception.() -> Unit): T =
    try {
        block(this)
        this
    } catch (e: Exception) {
        catchBlock(e)
        this
    }

inline fun <T, R> T.tryCatchLet(block: (T) -> R, catchBlock: Exception.() -> R): R =
    try {
        block(this)
    } catch (e: Exception) {
        catchBlock(e)
    }


inline fun tryCatch(block: () -> Unit) =
    try {
        block()
    } catch (_: Exception) {
    }

inline fun tryCatch(block: () -> Unit, catchBlock: Exception.() -> Unit) =
    try {
        block()
    } catch (e: Exception) {
        catchBlock(e)
    }

inline fun tryCatchUnit(block: () -> Unit, catchBlock: () -> Unit) =
    try {
        block()
    } catch (_: Exception) {
        catchBlock()
    }