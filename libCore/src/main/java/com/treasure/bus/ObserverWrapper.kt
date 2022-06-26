package com.treasure.bus

import android.arch.lifecycle.Observer
import com.treasure.bus.log.Logger
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description ObserverWrapper
 */
class ObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {
    internal var preventNextEvent = false
    override fun onChanged(t: T?) {
        if (preventNextEvent) {
            preventNextEvent = false
            return
        }
        Logger.log(Level.INFO, "message received: $t")
        try {
            observer.onChanged(t)
        } catch (e: ClassCastException) {
            Logger.log(
                Level.WARNING,
                "class cast error on message received: $t", e
            )
        } catch (e: Exception) {
            Logger.log(Level.WARNING, "error on message received: $t", e)
        }
    }
}