package com.treasure.bus

import com.treasure.bus.log.Logger

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description LiveBus
 */
object LiveBus {

    /**
     * 存放LiveEvent
     */
    internal var bus = HashMap<String, LiveEvent<Any>>()

    fun setLogger(logger: Logger) {
        Logger.setLogger(logger)
    }

    fun enableLog() {
        Logger.enableLog()
    }

    private var lifecycleObserverAlwaysActive = false
    private var autoClear = false

    @Synchronized
    fun <T>with(key: String): Observable<T> {
        if (!bus.containsKey(key)) {
            bus[key] = LiveEvent(key, lifecycleObserverAlwaysActive, autoClear)
        }
        return bus[key]!! as Observable<T>
    }

    fun setLifecycleObserverAlwaysActive(lifecycleObserverAlwaysActive: Boolean) {
        this.lifecycleObserverAlwaysActive = lifecycleObserverAlwaysActive
    }

    fun setAutoClear(autoClear: Boolean) {
        this.autoClear = autoClear
    }

}