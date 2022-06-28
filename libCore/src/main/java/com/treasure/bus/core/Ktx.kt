package com.treasure.bus.core

import android.os.Handler
import android.os.Looper
import com.treasure.bus.log.Logger
import java.util.logging.Level

private val mainHandler = Handler(Looper.getMainLooper())

/**
 * 保证操作在主线程中执行
 *
 * @param duration  延迟的时间
 * @param action    执行的操作
 * @return [Result]
 */
internal fun mainLaunch(duration: Long = 0L, action: () -> Unit) =
    runCatching {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            if (duration > 0) {
                mainHandler.postDelayed(action, duration)
            } else {
                action.invoke()
            }
        } else {
            if (duration > 0) {
                mainHandler.postDelayed(action, duration)
            } else {
                mainHandler.post(action)
            }
        }
    }.onFailure {
        it.printStackTrace()
        Logger.log(Level.WARNING, null, it)
    }