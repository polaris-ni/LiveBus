package com.treasure.bus.core

import androidx.lifecycle.Observer
import com.treasure.bus.log.Logger
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description ObserverWrapper
 */
internal class ObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {
    internal var shouldCallback = false
    override fun onChanged(t: T?) {
        if (shouldCallback) {
            shouldCallback = false
            return
        }
        Logger.log(Level.INFO, "message received: $t")
        try {
            observer.onChanged(t)
        } catch (e: ClassCastException) {
            Logger.log(Level.WARNING, "class cast error on message received: $t", e)
        } catch (e: Exception) {
            Logger.log(Level.WARNING, "error on message received: $t", e)
        }
    }
}