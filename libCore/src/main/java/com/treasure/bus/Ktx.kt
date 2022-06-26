package com.treasure.bus

import android.os.Looper
import android.util.Log
import com.treasure.bus.log.Logger
import kotlinx.coroutines.*
import java.util.logging.Level
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description Ktx
 */
fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

val mainScope: CoroutineScope = CoroutineScope(
    SupervisorJob()
            + Dispatchers.Main.immediate
            + CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow
)


/**
 * 具有异常处理机制并在主线程执行的协程启动器，适用于更新UI等操作
 * @param duration 延迟的时间
 * @param action 执行的协程体
 * @return Job
 */
fun mainLaunch(duration: Long = 0L, action: suspend (CoroutineScope) -> Unit) =
    mainScope.launch {
        delay(duration)
        action.invoke(this)
    }

/**
 * 设置协程异常策略的上下文元素
 */
object CoroutineExceptionHandlerWithReleaseUploadAndDebugThrow
    : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception !is CancellationException) {//如果是SupervisorJob就不会传播取消异常，而Job会传播
            Logger.log(Level.WARNING, "CoroutineExceptionHandler handleException: ", exception)
        }
    }
}