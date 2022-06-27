package com.treasure.bus.core

import android.os.Handler
import android.os.Looper
import com.treasure.bus.log.Logger
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description Ktx
 */
fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

private val mainHandler = Handler(Looper.getMainLooper())

/**
 * 具有异常处理机制并在主线程执行的协程启动器，适用于更新UI等操作
 * @param duration 延迟的时间
 * @param action 执行的协程体
 * @return Job
 */
fun mainLaunch(duration: Long = 0L, action: () -> Unit) =
    runCatching {
        if (isMainThread()) {
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
        Logger.log(Level.WARNING, null, it)
    }